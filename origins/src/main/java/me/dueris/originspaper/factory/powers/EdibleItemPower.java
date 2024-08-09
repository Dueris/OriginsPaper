package me.dueris.originspaper.factory.powers;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.modifier.Modifier;
import me.dueris.originspaper.data.types.modifier.ModifierUtil;
import me.dueris.originspaper.factory.action.ActionFactory;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
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
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

// TODO: ask paper why this freezes the eating process after eat seconds are over bc this is weird af
public class EdibleItemPower extends PowerType {
	private final ActionFactory<Entity> entityAction;
	private final ActionFactory<Tuple<Level, SlotAccess>> consumedItemAction;
	private final ActionFactory<Tuple<Level, SlotAccess>> resultItemAction;
	private final ConditionFactory<Tuple<Level, ItemStack>> itemCondition;
	private final FoodProperties foodComponent;
	private final ItemStack resultStack;
	private final ConsumeAnimation consumeAnimation;
	private final SoundEvent consumeSoundEvent;
	private final List<Modifier> consumingTimeModifiers;

	private final NamespacedKey EDIBLE_ITEM_MODIFIED_KEY = CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("edible_item_modified"));

	public EdibleItemPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
						   ActionFactory<Entity> entityAction, ActionFactory<Tuple<Level, SlotAccess>> consumedItemAction, ActionFactory<Tuple<Level, SlotAccess>> resultItemAction, ConditionFactory<Tuple<Level, ItemStack>> itemCondition,
						   FoodProperties foodComponent, ItemStack resultStack, ConsumeAnimation consumeAnimation, SoundEvent consumeSoundEvent, Modifier consumingTimeModifier, List<Modifier> consumingTimeModifiers) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.consumedItemAction = consumedItemAction;
		this.resultItemAction = resultItemAction;
		this.itemCondition = itemCondition;
		this.resultStack = resultStack;
		this.consumeAnimation = consumeAnimation;
		this.consumeSoundEvent = consumeSoundEvent;

		this.consumingTimeModifiers = new LinkedList<>();
		if (consumingTimeModifier != null) {
			this.consumingTimeModifiers.add(consumingTimeModifier);
		}

		if (consumingTimeModifiers != null) {
			this.consumingTimeModifiers.addAll(consumingTimeModifiers);
		}

		this.foodComponent = resolveFoodComponent(foodComponent);
	}

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("edible_item"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
			.add("food_component", SerializableDataTypes.FOOD_COMPONENT)
			.add("result_stack", SerializableDataTypes.ITEM_STACK, null)
			.add("consume_animation", SerializableDataTypes.enumValue(ConsumeAnimation.class), ConsumeAnimation.EAT)
			.add("consume_sound", SerializableDataTypes.SOUND_EVENT, SoundEvents.GENERIC_EAT)
			.add("consuming_time_modifier", Modifier.DATA_TYPE, null)
			.add("consuming_time_modifiers", Modifier.LIST_TYPE, null);
	}

	private FoodProperties resolveFoodComponent(@NotNull FoodProperties properties) {
		float seconds = properties.eatSeconds();
		Double newSeconds = ModifierUtil.applyModifiers(null, consumingTimeModifiers, seconds);

		// Convert to CraftBukkit equivalent to allow for easier modification of the food component
		CraftFoodComponent craft = new CraftFoodComponent(properties);
		craft.setEatSeconds(newSeconds.floatValue());
		return craft.getHandle();
	}

	public SlotAccess executeItemActions(SlotAccess consumedStack, Entity entity) {

		if (consumedItemAction != null) {
			consumedItemAction.accept(new Tuple<>(entity.level(), consumedStack));
		}

		SlotAccess resultStack = this.resultStack != null ? Util.createStackReference(this.resultStack.copy()) : SlotAccess.NULL;
		if (resultStack != SlotAccess.NULL && resultItemAction != null) {
			resultItemAction.accept(new Tuple<>(entity.level(), resultStack));
		}

		return resultStack;

	}

	@EventHandler
	public void actions(@NotNull PlayerItemConsumeEvent e) {
		net.minecraft.world.entity.player.Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		ItemStack original = CraftItemStack.unwrap(e.getItem());
		ItemStack result = original;
		modifyCustomFood:
		if (getPlayers().contains(player) && (itemCondition == null || itemCondition.test(new Tuple<>(player.level(), original)))) {
			if (entityAction != null) {
				entityAction.accept(player);
			}

			SlotAccess newStackRef = Util.createStackReference(original);
			SlotAccess resultStackRef = executeItemActions(newStackRef, player);

			ItemStack newStack = newStackRef.get();
			ItemStack resultStack = resultStackRef.get();

			if (resultStackRef == SlotAccess.NULL) {
				result = newStack;
				break modifyCustomFood;
			} else if (newStack.isEmpty()) {
				result = resultStack;
				break modifyCustomFood;
			} else if (ItemStack.matches(resultStack, newStack)) {
				newStack.grow(1);
			} else if (!player.isCreative()) {
				player.getInventory().placeItemBackInInventory(resultStack);
			} else {
				Util.throwItem(player, resultStack, false, false);
			}

			result = newStack;
		}

		e.setItem(result.getBukkitStack());
		player.playSound(consumeSoundEvent);
	}

	@EventHandler
	public void setFoodable(@NotNull PlayerItemHeldEvent e) {
		org.bukkit.inventory.ItemStack stack = e.getPlayer().getInventory().getItem(e.getNewSlot());
		if (stack != null) {
			Player p = e.getPlayer();
			if (getPlayers().contains(((CraftPlayer) p).getHandle()) && !stack.getItemMeta().getPersistentDataContainer().has(EDIBLE_ITEM_MODIFIED_KEY)) {
				if (!itemCondition.test(new Tuple<>(((CraftPlayer) p).getHandle().level(), CraftItemStack.unwrap(stack))))
					return;
				if (!isActive(((CraftPlayer) p).getHandle())) return;
				FoodComponent food = new CraftFoodComponent(foodComponent);
				ItemMeta meta = stack.getItemMeta();
				meta.getPersistentDataContainer().set(EDIBLE_ITEM_MODIFIED_KEY, PersistentDataType.BOOLEAN, true);
				meta.setFood(food);
				stack.setItemMeta(meta);
				e.getPlayer().getInventory().setItem(e.getNewSlot(), stack);
				return;
			}

			if (stack.getItemMeta().getPersistentDataContainer().has(EDIBLE_ITEM_MODIFIED_KEY)) {
				ItemMeta meta = stack.getItemMeta();
				meta.setFood(null);
				meta.getPersistentDataContainer().remove(EDIBLE_ITEM_MODIFIED_KEY);
				stack.setItemMeta(meta);
			}
		}
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
