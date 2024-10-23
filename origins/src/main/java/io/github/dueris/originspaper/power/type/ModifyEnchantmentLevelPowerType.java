package io.github.dueris.originspaper.power.type;

import com.mojang.datafixers.util.Pair;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class ModifyEnchantmentLevelPowerType extends ValueModifyingPowerType {

	@ApiStatus.Internal
	public static final ConcurrentHashMap<UUID, WeakHashMap<ItemStack, ItemStack>> COPY_TO_ORIGINAL_STACK = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<UUID, WeakHashMap<ItemStack, ItemEnchantments>> ITEM_ENCHANTMENTS = new ConcurrentHashMap<>();

	private static final ConcurrentHashMap<UUID, ItemStack> MODIFIED_EMPTY_STACKS = new ConcurrentHashMap<>();
	private static final WeakHashMap<Pair<UUID, ItemStack>, ConcurrentHashMap<ModifyEnchantmentLevelPowerType, Pair<Integer, Boolean>>> POWER_MODIFIER_CACHE = new WeakHashMap<>(256);

	private final ResourceKey<Enchantment> enchantmentKey;
	private final Predicate<Tuple<Level, ItemStack>> itemCondition;

	public ModifyEnchantmentLevelPowerType(Power power, LivingEntity entity, ResourceKey<Enchantment> enchantmentKey, Predicate<net.minecraft.util.Tuple<Level, ItemStack>> itemCondition, Modifier modifier, List<Modifier> modifiers) {
		super(power, entity);

		this.enchantmentKey = enchantmentKey;
		this.itemCondition = itemCondition;

		if (modifier != null) {
			this.addModifier(modifier);
		}

		if (modifiers != null) {
			modifiers.forEach(this::addModifier);
		}

		this.setTicking();

	}

	@ApiStatus.Internal // OriginsPaper
	private static ItemEnchantments getEnchantments(ItemStack stack, ItemEnchantments original, boolean modified) { // OriginsPaper - public -> private

		Entity entity = ((EntityLinkedItemStack) stack).apoli$getEntity();
		if (entity == null || !modified) {
			return original;
		}

		UUID uuid = entity.getUUID();
		ItemStack actualStack = COPY_TO_ORIGINAL_STACK.containsKey(uuid)
			? COPY_TO_ORIGINAL_STACK.get(uuid).getOrDefault(stack, stack)
			: stack;

		if (ITEM_ENCHANTMENTS.containsKey(uuid) && ITEM_ENCHANTMENTS.get(uuid).containsKey(actualStack)) {
			return ITEM_ENCHANTMENTS.get(uuid).get(actualStack);
		}

		return original;

	}

	public static ItemEnchantments getAndUpdateModifiedEnchantments(ItemStack stack, ItemEnchantments original, boolean modified) {
		Entity entity = ((EntityLinkedItemStack) stack).apoli$getEntity();

		if (entity instanceof LivingEntity living) {
			calculateLevels(living, stack);
		}

		return getEnchantments(stack, original, modified);
	}

	public static ItemEnchantments getAndUpdateModifiedEnchantments(ItemStack stack, ItemEnchantments original) {
		return getAndUpdateModifiedEnchantments(stack, original, true);
	}

	private static void calculateLevels(LivingEntity entity, ItemStack stack) {

		for (ModifyEnchantmentLevelPowerType power : PowerHolderComponent.getPowerTypes(entity, ModifyEnchantmentLevelPowerType.class)) {

			Pair<UUID, ItemStack> uuidAndStack = Pair.of(entity.getUUID(), stack);
			int baseModifiedLevel = (int) ModifierUtil.applyModifiers(entity, power.getModifiers(), 0);

			if (POWER_MODIFIER_CACHE.containsKey(uuidAndStack) && !updateIfDifferent(POWER_MODIFIER_CACHE.get(uuidAndStack), power, stack, baseModifiedLevel, power.doesApply(power.enchantmentKey, stack))) {
				continue;
			}

			//  If all modify enchantment powers are not active...
			if (ITEM_ENCHANTMENTS.containsKey(entity.getUUID()) && POWER_MODIFIER_CACHE.containsKey(uuidAndStack) && POWER_MODIFIER_CACHE.get(uuidAndStack).entrySet().stream().filter(entry -> entry.getKey().enchantmentKey.equals(power.enchantmentKey)).noneMatch(entry -> entry.getValue().getSecond())) {
				//  Remove the power's enchantments component
				ITEM_ENCHANTMENTS.get(entity.getUUID()).remove(stack);
				break;
			}

			ItemEnchantments.Mutable enchantmentsBuilder = new ItemEnchantments.Mutable(stack.getEnchantments());
			Set<Holder<Enchantment>> processedEnchantments = new HashSet<>();

			//  Iterate on all powers, because we found a match, and must set the item enchantments accordingly
			for (ModifyEnchantmentLevelPowerType innerPower : PowerHolderComponent.getPowerTypes(entity, ModifyEnchantmentLevelPowerType.class)) {

				Holder<Enchantment> innerEnchantment = entity.registryAccess()
					.registryOrThrow(Registries.ENCHANTMENT)
					.getHolderOrThrow(innerPower.enchantmentKey);

				//  If this enchantment has already been processed, continue
				if (processedEnchantments.contains(innerEnchantment)) {
					continue;
				}

				//  Set the enchantment level from all modify enchantment powers that have the enchantment
				int innerEnchantmentLevel = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).getLevel(innerEnchantment);
				enchantmentsBuilder.set(innerEnchantment, (int) PowerHolderComponent.modify(entity, ModifyEnchantmentLevelPowerType.class, innerEnchantmentLevel, p -> innerPower.doesApply(innerPower.enchantmentKey, stack)));

				//  Mark the enchantment as processed
				processedEnchantments.add(innerEnchantment);

			}

			power.recalculateCache(entity, stack);
			ITEM_ENCHANTMENTS
				.computeIfAbsent(entity.getUUID(), uuid -> new WeakHashMap<>())
				.put(stack, enchantmentsBuilder.toImmutable());

			break;

		}

	}

	public static ItemStack getOrCreateWorkableEmptyStack(Entity entity) {

		if (!PowerHolderComponent.hasPowerType(entity, ModifyEnchantmentLevelPowerType.class)) {
			return ItemStack.EMPTY;
		}

		UUID uuid = entity.getUUID();
		if (MODIFIED_EMPTY_STACKS.containsKey(uuid)) {
			return MODIFIED_EMPTY_STACKS.get(uuid);
		}

		ItemStack workableEmptyStack = new ItemStack((Void) null);
		((EntityLinkedItemStack) workableEmptyStack).apoli$setEntity(entity);

		return MODIFIED_EMPTY_STACKS.compute(uuid, (prevUuid, prevStack) -> workableEmptyStack);

	}

	public static void integrateCallback(Entity entity, Level world) {
		MODIFIED_EMPTY_STACKS.remove(entity.getUUID());
	}

	public static boolean isWorkableEmptyStack(SlotAccess stackReference) {
		Entity stackHolder = ((EntityLinkedItemStack) stackReference.get()).apoli$getEntity();
		return stackHolder != null && isWorkableEmptyStack(stackHolder, stackReference);
	}

	public static boolean isWorkableEmptyStack(ItemStack stack) {
		return stack.isEmpty() && MODIFIED_EMPTY_STACKS.contains(stack);
	}

	public static boolean isWorkableEmptyStack(@NotNull Entity entity, SlotAccess stackReference) {
		return stackReference.get().isEmpty()
			&& MODIFIED_EMPTY_STACKS.containsKey(entity.getUUID())
			&& stackReference.get() == MODIFIED_EMPTY_STACKS.get(entity.getUUID());
	}

	private static boolean updateIfDifferent(ConcurrentHashMap<ModifyEnchantmentLevelPowerType, Pair<Integer, Boolean>> map, ModifyEnchantmentLevelPowerType power, ItemStack stack, int modifierValue, boolean conditionValue) {

		map.computeIfAbsent(power, (p) -> new Pair<>(0, false));
		boolean value = false;

		if (map.get(power).getFirst() != modifierValue) {
			map.put(power, Pair.of(modifierValue, map.get(power).getSecond()));
			value = true;
		}

		if (map.get(power).getSecond() != conditionValue) {
			map.put(power, Pair.of(map.get(power).getFirst(), conditionValue));
			value = true;
		}

		return value;

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_enchantment_level"),
			new SerializableData()
				.add("enchantment", SerializableDataTypes.ENCHANTMENT)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null),
			data -> (power, entity) -> new ModifyEnchantmentLevelPowerType(power, entity,
				data.get("enchantment"),
				data.get("item_condition"),
				data.get("modifier"),
				data.get("modifiers")
			)
		).allowCondition();
	}

	@Override
	public void onRemoved() {

		for (int slot : InventoryUtil.getAllSlots()) {

			SlotAccess stackReference = entity.getSlot(slot);

			if (stackReference != SlotAccess.NULL && isWorkableEmptyStack(entity, stackReference)) {
				stackReference.set(ItemStack.EMPTY);
			}

		}

		COPY_TO_ORIGINAL_STACK.remove(entity.getUUID());
		ITEM_ENCHANTMENTS.remove(entity.getUUID());

		MODIFIED_EMPTY_STACKS.remove(entity.getUUID());

	}

	@Override
	public void tick() {

		for (int slot : InventoryUtil.getAllSlots()) {

			SlotAccess stackReference = entity.getSlot(slot);
			if (stackReference == SlotAccess.NULL) {
				continue;
			}

			ItemStack stack = stackReference.get();
			if (stack instanceof EntityLinkedItemStack linked) {
				linked.apoli$setEntity(entity);
			}
			if (stack.isEmpty() && !isWorkableEmptyStack(entity, stackReference) && checkItemCondition(stack)) {
				stackReference.set(getOrCreateWorkableEmptyStack(entity));
			}

		}

	}

	public boolean doesApply(ResourceKey<Enchantment> enchantmentKey, ItemStack stack) {
		return this.isActive()
			&& this.enchantmentKey.equals(enchantmentKey)
			&& this.checkItemCondition(stack);
	}

	public void recalculateCache(LivingEntity entity, ItemStack stack) {

		for (ModifyEnchantmentLevelPowerType power : PowerHolderComponent.getPowerTypes(entity, ModifyEnchantmentLevelPowerType.class)) {

			ConcurrentHashMap<ModifyEnchantmentLevelPowerType, Pair<Integer, Boolean>> cacheMap = new ConcurrentHashMap<>();
			cacheMap.put(power, new Pair<>((int) ModifierUtil.applyModifiers(entity, power.getModifiers(), 0), power.doesApply(power.enchantmentKey, stack)));

			POWER_MODIFIER_CACHE.put(Pair.of(entity.getUUID(), stack), cacheMap);

		}

	}

	public boolean checkItemCondition(ItemStack self) {
		return itemCondition == null || itemCondition.test(new net.minecraft.util.Tuple<>(entity.level(), self));
	}

}
