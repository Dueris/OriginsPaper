package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.data.types.Modifier;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class EdibleItem extends PowerType {
	private final FactoryJsonObject entityAction;
	private final FactoryJsonObject itemAction;
	private final FactoryJsonObject resultItemAction;
	private final FactoryJsonObject itemCondition;
	private final FactoryJsonObject foodComponent;
	private final ItemStack resultStack;
	private final Modifier[] modifiers;
	private final NamespacedKey EDIBLE_ITEM_MODIFIED_KEY = CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("edible_item_modified"));

	public EdibleItem(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction, FactoryJsonObject itemAction, FactoryJsonObject resultItemAction, FactoryJsonObject itemCondition, FactoryJsonObject foodComponent, ItemStack resultStack, FactoryJsonObject consumingTimeModifier, FactoryJsonArray consumingTimeModifiers) {
		super(name, description, hidden, condition, loading_priority);
		this.entityAction = entityAction;
		this.itemAction = itemAction;
		this.resultItemAction = resultItemAction;
		this.itemCondition = itemCondition;
		this.foodComponent = foodComponent;
		this.resultStack = resultStack;
		this.modifiers = Modifier.getModifiers(consumingTimeModifier, consumingTimeModifiers);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("edible_item"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("result_item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("food_component", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("result_stack", ItemStack.class, new OptionalInstance())
			.add("consuming_time_modifier", FactoryJsonObject.class, new OptionalInstance())
			.add("consuming_time_modifiers", FactoryJsonArray.class, new OptionalInstance());
	}

	@EventHandler
	public void actions(@NotNull PlayerItemConsumeEvent e) {
		if (e.getItem() != null && getPlayers().contains(e.getPlayer())) {
			if (!ConditionExecutor.testItem(itemCondition, e.getItem())) return;
			if (!isActive(e.getPlayer())) return;
			new BukkitRunnable() {
				@Override
				public void run() {
					if (resultStack != null) {
						Actions.executeItem(resultStack, e.getPlayer().getWorld(), resultItemAction);
						e.getPlayer().getInventory().addItem(resultStack);
					}
					Actions.executeEntity(e.getPlayer(), entityAction);
					Actions.executeItem(e.getItem(), e.getPlayer().getWorld(), itemAction);
				}
			}.runTaskLater(OriginsPaper.getPlugin(), 1);
		}
	}

	@EventHandler
	public void setFoodable(@NotNull PlayerItemHeldEvent e) {
		ItemStack stack = e.getPlayer().getInventory().getItem(e.getNewSlot());
		if (stack != null) {
			if (getPlayers().contains(e.getPlayer()) && !stack.getItemMeta().getPersistentDataContainer().has(EDIBLE_ITEM_MODIFIED_KEY)) {
				Player p = e.getPlayer();
				if (!ConditionExecutor.testItem(itemCondition, stack)) return;
				if (!isActive(p)) return;
				FoodComponent food = Util.parseProperties(foodComponent);
				float s = food.getEatSeconds();
				Arrays.stream(modifiers).forEach(modifier -> {
					Util.getOperationMappingsFloat().get(modifier.operation()).apply(s, modifier.value());
				});
				food.setEatSeconds(s);
				ItemMeta meta = stack.getItemMeta();
				meta.getPersistentDataContainer().set(EDIBLE_ITEM_MODIFIED_KEY, PersistentDataType.BOOLEAN, true);
				meta.setFood(food);
				stack.setItemMeta(meta);
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

}
