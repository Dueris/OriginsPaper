package io.github.dueris.originspaper.power.type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.InventoryUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.components.CraftFoodComponent;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EdibleItemPowerType extends PowerType implements Prioritized<EdibleItemPowerType>, Listener {

	public static final TypedDataObjectFactory<EdibleItemPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
			.add("result_item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
			.add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
			.add("food_component", SerializableDataTypes.FOOD_COMPONENT)
			.add("result_stack", SerializableDataTypes.ITEM_STACK.optional(), Optional.empty())
			.add("consume_animation", SerializableDataType.enumValue(UseAnim.class), UseAnim.EAT)
			.add("consume_sound", SerializableDataTypes.SOUND_EVENT, SoundEvents.GENERIC_EAT)
			.add("priority", SerializableDataTypes.INT, 0),
		(data, condition) -> new EdibleItemPowerType(
			data.get("entity_action"),
			data.get("item_action"),
			data.get("result_item_action"),
			data.get("item_condition"),
			data.get("food_component"),
			data.get("result_stack"),
			data.get("consume_animation"),
			data.get("consume_sound"),
			data.get("priority"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
			.set("item_action", powerType.consumedItemAction)
			.set("result_item_action", powerType.resultItemAction)
			.set("item_condition", powerType.itemCondition)
			.set("food_component", powerType.foodComponent)
			.set("result_stack", powerType.resultStack)
			.set("consume_animation", powerType.consumeAnimation)
			.set("consume_sound", powerType.consumeSoundEvent)
			.set("priority", powerType.getPriority())
	);

	private final Optional<EntityAction> entityAction;
	private final Optional<ItemAction> resultItemAction;
	private final Optional<ItemAction> consumedItemAction;

	private final Optional<ItemCondition> itemCondition;

	private final FoodProperties foodComponent;
	private final Optional<ItemStack> resultStack;
	private final UseAnim consumeAnimation;
	private final SoundEvent consumeSoundEvent;

	private final int priority;
	private final NamespacedKey EDIBLE_ITEM_MODIFIED_KEY = CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("edible_item_modified"));
	private final NamespacedKey EDIBLE_ITEM_ORIGINAL_KEY = CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("edible_item_original"));

	public EdibleItemPowerType(Optional<EntityAction> entityAction, Optional<ItemAction> consumedItemAction, Optional<ItemAction> resultItemAction, Optional<ItemCondition> itemCondition, FoodProperties foodComponent, Optional<ItemStack> resultStack, UseAnim consumeAnimation, SoundEvent consumeSoundEvent, int priority, Optional<EntityCondition> condition) {
		super(condition);
		this.entityAction = entityAction;
		this.consumedItemAction = consumedItemAction;
		this.resultItemAction = resultItemAction;
		this.itemCondition = itemCondition;
		this.foodComponent = foodComponent;
		this.resultStack = resultStack;
		this.consumeAnimation = consumeAnimation;
		this.consumeSoundEvent = consumeSoundEvent;
		this.priority = priority;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.EDIBLE_ITEM;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public static @NotNull Optional<EdibleItemPowerType> get(ItemStack stack, @Nullable Entity holder) {
		return PowerHolderComponent.getPowerTypes(holder, EdibleItemPowerType.class)
			.stream()
			.filter(p -> p.doesApply(stack))
			.max(Comparator.comparing(EdibleItemPowerType::getPriority));
	}

	public static @NotNull Optional<EdibleItemPowerType> get(ItemStack stack) {
		Entity stackHolder = ((EntityLinkedItemStack) stack).apoli$getEntity(true);
		return get(stack, stackHolder);
	}

	@EventHandler
	public void setFoodable(@NotNull PlayerItemHeldEvent e) {
		org.bukkit.inventory.ItemStack stack = e.getPlayer().getInventory().getItem(e.getNewSlot());
		if (stack != null) {
			Player p = e.getPlayer();
			boolean isModified = stack.getItemMeta().getPersistentDataContainer().has(EDIBLE_ITEM_MODIFIED_KEY);
			boolean isValid = getHolder() == ((CraftPlayer) p).getHandle();
			boolean conditions = !(itemCondition.orElse(null) == null ? true : itemCondition.get().test(getHolder().level(), CraftItemStack.unwrap(stack))) || !isActive();
			if (isValid && !isModified && conditions) {
				ItemMeta meta = stack.getItemMeta();
				FoodComponent food = new CraftFoodComponent(foodComponent);
				meta.getPersistentDataContainer().set(EDIBLE_ITEM_MODIFIED_KEY, PersistentDataType.BOOLEAN, true);
				meta.getPersistentDataContainer().set(EDIBLE_ITEM_ORIGINAL_KEY, PersistentDataType.STRING,
					meta.hasFood() ? SerializableDataTypes.FOOD_COMPONENT.codec().encodeStart(JsonOps.INSTANCE, ((CraftFoodComponent) meta.getFood()).getHandle()).getOrThrow().toString() : "null");
				meta.setFood(food);
				stack.setItemMeta(meta);
				e.getPlayer().getInventory().setItem(e.getNewSlot(), stack);
				return;
			}

			if (isModified && !(isValid || conditions)) {
				clear(stack);
			}
		}

		stack = e.getPlayer().getInventory().getItem(e.getPreviousSlot());
		if (stack != null) {
			e.getPlayer().getInventory().setItem(e.getPreviousSlot(), clear(stack));
		}
	}

	public org.bukkit.inventory.ItemStack clear(org.bukkit.inventory.@NotNull ItemStack stack) {
		if (stack.getItemMeta() == null) return stack;
		boolean isModified = stack.getItemMeta().getPersistentDataContainer().has(EDIBLE_ITEM_MODIFIED_KEY);
		if (isModified) {
			ItemMeta meta = stack.getItemMeta();
			String stringData = meta.getPersistentDataContainer().get(EDIBLE_ITEM_ORIGINAL_KEY, PersistentDataType.STRING);
			meta.setFood(stringData.equalsIgnoreCase("null") ? null : new CraftFoodComponent(SerializableDataTypes.FOOD_COMPONENT.codec().decode(
				JsonOps.INSTANCE, new Gson().fromJson(stringData, JsonElement.class)
			).getOrThrow().getFirst()));
			meta.getPersistentDataContainer().remove(EDIBLE_ITEM_MODIFIED_KEY);
			meta.getPersistentDataContainer().remove(EDIBLE_ITEM_ORIGINAL_KEY);
			stack.setItemMeta(meta);
		}
		return stack;
	}

	public void clearAll() {
		clear(getHolder().getMainHandItem().asBukkitMirror());
		clear(getHolder().getOffhandItem().asBukkitMirror());
	}

	@Override
	public void onRemoved() {
		clearAll();
	}

	@Override
	public void onLeave() {
		clearAll();
	}

	public boolean doesApply(ItemStack stack) {
		return itemCondition
			.map(condition -> condition.test(getHolder().level(), stack))
			.orElse(true);
	}

	public void executeEntityAction() {
		entityAction.ifPresent(action -> action.execute(getHolder()));
	}

	public SlotAccess executeItemActions(SlotAccess consumedStackReference) {

		LivingEntity holder = getHolder();
		Level world = holder.level();

		consumedItemAction.ifPresent(action -> action.execute(world, consumedStackReference));

		SlotAccess resultStackReference = this.resultStack
			.map(ItemStack::copy)
			.map(InventoryUtil::createStackReference)
			.orElse(SlotAccess.NULL);

		resultItemAction.ifPresent(action -> action.execute(world, resultStackReference));
		return resultStackReference;

	}

	public FoodProperties getFoodComponent() {
		return foodComponent;
	}

	public UseAnim getConsumeAnimation() {
		return consumeAnimation;
	}

	public SoundEvent getConsumeSoundEvent() {
		return consumeSoundEvent;
	}

	public enum ConsumeAnimation {

		EAT(UseAnim.EAT),
		DRINK(UseAnim.DRINK);

		final UseAnim action;

		ConsumeAnimation(UseAnim action) {
			this.action = action;
		}

		public UseAnim getAction() {
			return action;
		}

	}

}
