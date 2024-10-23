package io.github.dueris.originspaper.power.type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
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

	private final Consumer<Entity> entityAction;
	private final Consumer<Tuple<Level, SlotAccess>> resultItemAction;
	private final Consumer<Tuple<Level, SlotAccess>> consumedItemAction;

	private final Predicate<Tuple<Level, ItemStack>> itemCondition;

	private final FoodProperties foodComponent;
	private final ItemStack resultStack;
	private final ConsumeAnimation consumeAnimation;
	private final SoundEvent consumeSoundEvent;

	private final int priority;
	private final NamespacedKey EDIBLE_ITEM_MODIFIED_KEY = CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("edible_item_modified"));
	private final NamespacedKey EDIBLE_ITEM_ORIGINAL_KEY = CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("edible_item_original"));

	public EdibleItemPowerType(Power power, LivingEntity entity, Consumer<Entity> entityAction, Consumer<Tuple<Level, SlotAccess>> consumedItemAction, Consumer<Tuple<Level, SlotAccess>> resultItemAction, Predicate<Tuple<Level, ItemStack>> itemCondition, FoodProperties foodComponent, ItemStack resultStack, ConsumeAnimation consumeAnimation, SoundEvent consumeSoundEvent, int priority) {
		super(power, entity);
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

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("edible_item"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("food_component", SerializableDataTypes.FOOD_COMPONENT)
				.add("return_stack", SerializableDataTypes.ITEM_STACK, null)
				.addFunctionedDefault("result_stack", SerializableDataTypes.ITEM_STACK, data -> data.get("return_stack"))
				.add("use_action", SerializableDataType.enumValue(ConsumeAnimation.class), ConsumeAnimation.EAT)
				.addFunctionedDefault("consume_animation", SerializableDataType.enumValue(ConsumeAnimation.class), data -> data.get("use_action"))
				.add("sound", SerializableDataTypes.SOUND_EVENT, SoundEvents.GENERIC_EAT)
				.addFunctionedDefault("consume_sound", SerializableDataTypes.SOUND_EVENT, data -> data.get("sound"))
				.add("priority", SerializableDataTypes.INT, 0),
			data -> (power, entity) -> new EdibleItemPowerType(
				power,
				entity,
				data.get("entity_action"),
				data.get("item_action"),
				data.get("result_item_action"),
				data.get("item_condition"),
				data.get("food_component"),
				data.get("result_stack"),
				data.get("consume_animation"),
				data.get("consume_sound"),
				data.get("priority")
			)
		).allowCondition();
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
			boolean isValid = entity == ((CraftPlayer) p).getHandle();
			boolean conditions = !(!itemCondition.test(new Tuple<>(((CraftPlayer) p).getHandle().level(), CraftItemStack.unwrap(stack))) || !isActive());
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
		clear(entity.getMainHandItem().asBukkitMirror());
		clear(entity.getOffhandItem().asBukkitMirror());
	}

	@Override
	public void onRemoved() {
		clearAll();
	}

	@Override
	public void onLeave() {
		clearAll();
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean doesApply(ItemStack stack) {
		return itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack));
	}

	public void executeEntityAction() {
		if (entityAction != null) {
			entityAction.accept(entity);
		}
	}

	public SlotAccess executeItemActions(SlotAccess consumedStack) {

		if (consumedItemAction != null) {
			consumedItemAction.accept(new Tuple<>(entity.level(), consumedStack));
		}

		SlotAccess resultStack = this.resultStack != null ? InventoryUtil.createStackReference(this.resultStack.copy()) : SlotAccess.NULL;
		if (resultStack != SlotAccess.NULL && resultItemAction != null) {
			resultItemAction.accept(new Tuple<>(entity.level(), resultStack));
		}

		return resultStack;

	}

	public FoodProperties getFoodComponent() {
		return foodComponent;
	}

	public ConsumeAnimation getConsumeAnimation() {
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
