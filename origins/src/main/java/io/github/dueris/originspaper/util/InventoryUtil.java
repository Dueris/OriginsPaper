package io.github.dueris.originspaper.util;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.ArgumentWrapper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.mixin.SlotRangesAccessor;
import io.github.dueris.originspaper.power.type.InventoryPowerType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class InventoryUtil {

	private static final List<String> EXEMPT_SLOTS = List.of("weapon", "weapon.mainhand");

	public static @NotNull Set<Integer> getSlots(SerializableData.@NotNull Instance data) {

		Set<Integer> slots = new HashSet<>();

		data.<ArgumentWrapper<Integer>>ifPresent("slot", iaw -> slots.add(iaw.argument()));
		data.<List<ArgumentWrapper<Integer>>>ifPresent("slots", iaws -> slots.addAll(iaws.stream().map(ArgumentWrapper::argument).toList()));

		if (slots.isEmpty()) {
			slots.addAll(getAllSlots());
		}

		return slots;

	}

	public static int checkInventory(SerializableData.@NotNull Instance data, Entity entity, @Nullable InventoryPowerType inventoryPower, Function<ItemStack, Integer> processor) {

		Predicate<Tuple<Level, ItemStack>> itemCondition = data.get("item_condition");
		Set<Integer> slots = getSlots(data);
		deduplicateSlots(entity, slots);

		int matches = 0;
		slots.removeIf(slot -> slotNotWithinBounds(entity, inventoryPower, slot));

		for (int slot : slots) {

			SlotAccess stackReference = getStackReference(entity, inventoryPower, slot);
			ItemStack stack = stackReference.get();

			if ((itemCondition == null && !stack.isEmpty()) || (itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack)))) {
				matches += processor.apply(stack);
			}

		}

		return matches;

	}

	public static void modifyInventory(SerializableData.Instance data, Entity entity, InventoryPowerType inventoryPower, Function<ItemStack, Integer> processor, int limit) {

		if (limit <= 0) {
			limit = Integer.MAX_VALUE;
		}

		Set<Integer> slots = getSlots(data);
		deduplicateSlots(entity, slots);

		Consumer<Entity> entityAction = data.get("entity_action");
		Predicate<Tuple<Level, ItemStack>> itemCondition = data.get("item_condition");
		ActionTypeFactory<Tuple<Level, SlotAccess>>.Instance itemAction = data.get("item_action");

		int processedItems = 0;
		slots.removeIf(slot -> slotNotWithinBounds(entity, inventoryPower, slot));

		modifyingItemsLoop:
		for (int slot : slots) {

			SlotAccess stack = getStackReference(entity, inventoryPower, slot);
			if (!(itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack.get())))) {
				continue;
			}

			int amount = processor.apply(stack.get());
			for (int i = 0; i < amount; i++) {

				if (entityAction != null) {
					entityAction.accept(entity);
				}

				itemAction.accept(new Tuple<>(entity.level(), stack));
				++processedItems;

				if (processedItems >= limit) {
					break modifyingItemsLoop;
				}

			}

		}

	}

	public static void replaceInventory(SerializableData.Instance data, Entity entity, InventoryPowerType inventoryPower) {

		Set<Integer> slots = getSlots(data);
		deduplicateSlots(entity, slots);

		Consumer<Entity> entityAction = data.get("entity_action");
		Predicate<Tuple<Level, ItemStack>> itemCondition = data.get("item_condition");
		Consumer<Tuple<Level, SlotAccess>> itemAction = data.get("item_action");

		ItemStack replacementStack = data.get("stack");
		boolean mergeNbt = data.getBoolean("merge_nbt");

		slots.removeIf(slot -> slotNotWithinBounds(entity, inventoryPower, slot));
		for (int slot : slots) {

			SlotAccess stackReference = getStackReference(entity, inventoryPower, slot);
			ItemStack stack = stackReference.get();

			if (!(itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack)))) {
				continue;
			}

			if (entityAction != null) {
				entityAction.accept(entity);
			}

			ItemStack stackAfterReplacement = replacementStack.copy();
			if (mergeNbt) {
				CompoundTag orgNbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
				CustomData.update(DataComponents.CUSTOM_DATA, stackAfterReplacement, repNbt -> repNbt.merge(orgNbt));
			}

			stackReference.set(stackAfterReplacement);
			if (itemAction != null) {
				itemAction.accept(new Tuple<>(entity.level(), stackReference));
			}

		}

	}

	public static void dropInventory(SerializableData.Instance data, Entity entity, InventoryPowerType inventoryPower) {

		Set<Integer> slots = getSlots(data);
		deduplicateSlots(entity, slots);

		int amount = data.getInt("amount");
		boolean throwRandomly = data.getBoolean("throw_randomly");
		boolean retainOwnership = data.getBoolean("retain_ownership");

		Consumer<Entity> entityAction = data.get("entity_action");
		Predicate<Tuple<Level, ItemStack>> itemCondition = data.get("item_condition");
		Consumer<Tuple<Level, SlotAccess>> itemAction = data.get("item_action");

		slots.removeIf(slot -> slotNotWithinBounds(entity, inventoryPower, slot));
		for (int slot : slots) {

			SlotAccess stack = getStackReference(entity, inventoryPower, slot);
			if (stack.get().isEmpty() || !(itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack.get())))) {
				continue;
			}

			if (entityAction != null) {
				entityAction.accept(entity);
			}

			if (itemAction != null) {
				itemAction.accept(new Tuple<>(entity.level(), stack));
			}

			ItemStack newStack = stack.get();
			ItemStack droppedStack = ItemStack.EMPTY;
			if (amount != 0) {
				int newAmount = amount < 0 ? amount * -1 : amount;
				droppedStack = newStack.split(newAmount);
			}

			throwItem(entity, droppedStack.isEmpty() ? stack.get() : droppedStack, throwRandomly, retainOwnership);
			stack.set(droppedStack.isEmpty() ? ItemStack.EMPTY : newStack);

		}

	}

	public static void throwItem(Entity thrower, @NotNull ItemStack itemStack, boolean throwRandomly, boolean retainOwnership) {

		if (itemStack.isEmpty()) {
			return;
		}

		if (thrower instanceof Player playerEntity && playerEntity.level().isClientSide) {
			playerEntity.swing(InteractionHand.MAIN_HAND);
		}

		double yOffset = thrower.getEyeY() - 0.30000001192092896D;
		ItemEntity itemEntity = new ItemEntity(thrower.level(), thrower.getX(), yOffset, thrower.getZ(), itemStack);
		itemEntity.setPickUpDelay(40);

		Random random = new Random();

		float f;
		float g;

		if (retainOwnership) itemEntity.setThrower(thrower);
		if (throwRandomly) {
			f = random.nextFloat() * 0.5F;
			g = random.nextFloat() * 6.2831855F;
			itemEntity.setDeltaMovement(-Mth.sin(g) * f, 0.20000000298023224D, Mth.cos(g) * f);
		} else {
			f = 0.3F;
			g = Mth.sin(thrower.getXRot() * 0.017453292F);
			float h = Mth.cos(thrower.getXRot() * 0.017453292F);
			float i = Mth.sin(thrower.getYRot() * 0.017453292F);
			float j = Mth.cos(thrower.getYRot() * 0.017453292F);
			float k = random.nextFloat() * 6.2831855F;
			float l = 0.02F * random.nextFloat();
			itemEntity.setDeltaMovement(
				(double) (-i * h * f) + Math.cos(k) * (double) l,
				(-g * f + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F),
				(double) (j * h * f) + Math.sin(k) * (double) l
			);
		}

		thrower.level().addFreshEntity(itemEntity);

	}

	public static void forEachStack(Entity entity, Consumer<ItemStack> itemStackConsumer) {

		int slotToSkip = getDuplicatedSlotIndex(entity);
		for (int slot : getAllSlots()) {

			if (slot == slotToSkip) {
				slotToSkip = Integer.MIN_VALUE;
				continue;
			}

			SlotAccess stackReference = entity.getSlot(slot);
			if (stackReference == SlotAccess.NULL) {
				continue;
			}

			ItemStack stack = stackReference.get();
			if (!stack.isEmpty()) {
				itemStackConsumer.accept(stack);
			}

		}

		PowerHolderComponent component = PowerHolderComponent.KEY.maybeGet(entity).orElse(null);
		if (component == null) {
			return;
		}

		List<InventoryPowerType> inventoryPowers = component.getPowerTypes(InventoryPowerType.class);
		for (InventoryPowerType inventoryPower : inventoryPowers) {
			for (int index = 0; index < inventoryPower.getContainerSize(); index++) {

				ItemStack stack = inventoryPower.getItem(index);
				if (!stack.isEmpty()) {
					itemStackConsumer.accept(stack);
				}

			}
		}

	}

	public static SlotAccess getStackReferenceFromStack(Entity entity, ItemStack stack) {
		return getStackReferenceFromStack(entity, stack, (provStack, refStack) -> provStack == refStack);
	}

	public static SlotAccess getStackReferenceFromStack(Entity entity, ItemStack stack, BiPredicate<ItemStack, ItemStack> equalityPredicate) {

		int slotToSkip = getDuplicatedSlotIndex(entity);
		for (int slot : getAllSlots()) {

			if (slot == slotToSkip) {
				slotToSkip = Integer.MIN_VALUE;
				continue;
			}

			SlotAccess stackReference = entity.getSlot(slot);
			if (stackReference != SlotAccess.NULL && equalityPredicate.test(stack, stackReference.get())) {
				return stackReference;
			}

		}

		return SlotAccess.NULL;

	}

	private static void deduplicateSlots(Entity entity, Set<Integer> slots) {

		int selectedHotbarSlot = getDuplicatedSlotIndex(entity);
		if (selectedHotbarSlot != Integer.MIN_VALUE && slots.contains(selectedHotbarSlot)) {
			SlotRangesAccessor.getSlotRanges()
				.stream()
				.filter(sr -> EXEMPT_SLOTS.contains(sr.getSerializedName()))
				.flatMapToInt(sr -> sr.slots().intStream())
				.forEach(slots::remove);
		}

	}

	/**
	 * <p>For players, their selected hotbar slot will overlap with the `weapon.mainhand` slot reference. This
	 * method returns the slot ID of the selected hotbar slot.</p>
	 *
	 * @param entity The entity to get the slot ID of its selected hotbar slot
	 * @return The slot ID of the hotbar slot or {@link Integer#MIN_VALUE} if the entity is not a player
	 */
	private static int getDuplicatedSlotIndex(Entity entity) {

		SlotRange slotRange = entity instanceof Player player
			? SlotRanges.nameToIds("hotbar." + player.getInventory().selected)
			: null;

		return slotRange != null
			? slotRange.slots().getFirst()
			: Integer.MIN_VALUE;

	}

	/**
	 * <p>Check whether the specified slot is <b>not</b> within the bounds of the entity's {@linkplain
	 * SlotAccess stack reference} or the specified {@link InventoryPowerType}.</p>
	 *
	 * @param entity         The entity check the bounds of its {@linkplain SlotAccess stack reference}
	 * @param inventoryPower The {@link InventoryPowerType} to check the bounds of
	 * @param slot           The slot
	 * @return {@code true} if the slot is within the bounds of the {@linkplain
	 * SlotAccess stack reference} or the {@link InventoryPowerType}
	 */
	public static boolean slotNotWithinBounds(Entity entity, @Nullable InventoryPowerType inventoryPower, int slot) {
		return inventoryPower == null ? entity.getSlot(slot) == SlotAccess.NULL
			: slot < 0 || slot >= inventoryPower.getContainerSize();
	}

	/**
	 * <p>Get the stack reference from the entity or frin the inventory of the specified {@link InventoryPowerType} (if it's not null).</p>
	 *
	 * <p><b>Make sure to only call this method after you filter out the slots that aren't within the bounds
	 * of the entity's {@linkplain SlotAccess stack reference} or {@link InventoryPowerType} using {@link
	 * #slotNotWithinBounds(Entity, InventoryPowerType, int)}</b></p>
	 *
	 * @param entity         The entity to get the item stack from its {@linkplain SlotAccess stack reference}
	 * @param inventoryPower The {@link InventoryPowerType} to get the item stack from (can be null)
	 * @param slot           The (numerical) slot to get the item stack from
	 * @return The stack reference of the specified slot
	 */
	public static SlotAccess getStackReference(Entity entity, @Nullable InventoryPowerType inventoryPower, int slot) {
		return inventoryPower == null ? entity.getSlot(slot) : SlotAccess.forContainer(inventoryPower, slot);
	}

	/**
	 * <p>Get the item stack from the entity's {@linkplain SlotAccess stack reference} or the inventory of
	 * the specified {@link InventoryPowerType} (if it's not null).</p>
	 *
	 * <p><b>Make sure to only call this method after you filter out the slots that aren't within the bounds
	 * of the entity's {@linkplain SlotAccess stack reference} or {@link InventoryPowerType} using {@link
	 * #slotNotWithinBounds(Entity, InventoryPowerType, int)}</b></p>
	 *
	 * @param entity         The entity to get the item stack from its {@linkplain SlotAccess stack reference}
	 * @param inventoryPower The {@link InventoryPowerType} to get the item stack from (can be null)
	 * @param slot           The (numerical) slot to get the item stack from
	 * @return The item stack from the specified slot
	 */
	@Deprecated(forRemoval = true)
	public static ItemStack getStack(Entity entity, @Nullable InventoryPowerType inventoryPower, int slot) {
		return inventoryPower == null ? entity.getSlot(slot).get() : inventoryPower.getItem(slot);
	}

	/**
	 * <p>Set the item stack on the specified slot of the entity's {@linkplain SlotAccess stack reference}
	 * or the inventory of the specified {@link InventoryPowerType} (if it's not null).</p>
	 *
	 * <p><b>Make sure to only call this method after you filter out the slots that aren't within the bounds
	 * of the entity's {@linkplain SlotAccess stack reference} or {@link InventoryPowerType} using {@link
	 * #slotNotWithinBounds(Entity, InventoryPowerType, int)}</b></p>
	 *
	 * @param entity         The entity to modify the {@linkplain SlotAccess stack reference} of
	 * @param inventoryPower The {@link InventoryPowerType} to set the item stack to (can be null)
	 * @param stack          The item stack to set to the specified slot
	 * @param slot           The (numerical) slot to set the item stack to
	 */
	@Deprecated(forRemoval = true)
	public static void setStack(Entity entity, InventoryPowerType inventoryPower, ItemStack stack, int slot) {
		if (inventoryPower == null) {
			entity.getSlot(slot).set(stack);
		} else {
			inventoryPower.setItem(slot, stack);
		}
	}

	/**
	 * <p>Creates a stack reference that is not linked to any entity for use with item actions.</p>
	 *
	 * <p>Recommended for usage when either you don't have an entity for this operation, or you
	 * don't want to set the entity's StackReference.</p>
	 *
	 * @param startingStack The ItemStack that this reference will start with.
	 * @return A {@linkplain SlotAccess} that contains an ItemStack.
	 */
	public static @NotNull SlotAccess createStackReference(ItemStack startingStack) {
		return new SlotAccess() {

			ItemStack stack = startingStack;

			@Override
			public @NotNull ItemStack get() {
				return stack;
			}

			@Override
			public boolean set(@NotNull ItemStack stack) {
				this.stack = stack;
				return true;
			}

		};
	}

	public static List<Integer> getAllSlots() {
		return SlotRangesAccessor.getSlotRanges()
			.stream()
			.flatMapToInt(slotRange -> slotRange.slots().intStream())
			.boxed()
			.toList();
	}

	@Nullable
	public static Integer getSlotFromStackReference(Entity entity, SlotAccess stackReference) {

		for (int slot : getAllSlots()) {

			SlotAccess queriedStackRef = entity.getSlot(slot);

			if (queriedStackRef != SlotAccess.NULL && queriedStackRef.equals(stackReference)) {
				return slot;
			}

		}

		return null;

	}

	public enum InventoryType {
		INVENTORY,
		POWER
	}

	public enum ProcessMode {
		STACKS(stack -> 1),
		ITEMS(ItemStack::getCount);

		private final Function<ItemStack, Integer> processor;

		ProcessMode(Function<ItemStack, Integer> processor) {
			this.processor = processor;
		}

		public Function<ItemStack, Integer> getProcessor() {
			return processor;
		}
	}

}
