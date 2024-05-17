package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class EdibleItem extends PowerType {

	private final FactoryJsonObject entityAction;
	private final FactoryJsonObject itemAction;
	private final FactoryJsonObject resultItemAction;
	private final FactoryJsonObject itemCondition;
	private final FactoryJsonObject foodComponent;
	private final ItemStack resultStack;
	private final Modifier[] modifiers;

	public EdibleItem(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction, FactoryJsonObject itemAction,
					  FactoryJsonObject resultItemAction, FactoryJsonObject itemCondition, FactoryJsonObject foodComponent, ItemStack resultStack, FactoryJsonObject consumingTimeModifier,
					  FactoryJsonArray consumingTimeModifiers) {
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
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("edible_item"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("result_item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("food_component", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("result_stack", ItemStack.class, new OptionalInstance())
			.add("consuming_time_modifier", FactoryJsonObject.class, new OptionalInstance())
			.add("consuming_time_modifiers", FactoryJsonObject.class, new OptionalInstance());
	}

	@EventHandler
	public void actions(PlayerItemConsumeEvent e) {
		if (e.getItem() != null && getPlayers().contains(e.getPlayer())) {
			if (!ConditionExecutor.testItem(itemCondition, e.getItem())) return;
			if (!isActive(e.getPlayer())) return;
			new BukkitRunnable() {
				@Override
				public void run() {
					if (resultStack != null) {
						Actions.executeItem(resultStack, resultItemAction);
						e.getPlayer().getInventory().addItem(resultStack);
					}
					Actions.executeEntity(e.getPlayer(), entityAction);
					Actions.executeItem(e.getItem(), itemAction);
				}
			}.runTaskLater(GenesisMC.getPlugin(), 1);
		}
	}

	@EventHandler
	public void setFoodable(PlayerItemHeldEvent e) {
		ItemStack stack = e.getPlayer().getInventory().getItem(e.getNewSlot());
		if (stack != null) {
			if (getPlayers().contains(e.getPlayer()) && !stack.getItemMeta().getPersistentDataContainer().has(GenesisMC.apoliIdentifier("edible_item_modified"))) {
				Player p = e.getPlayer();
				if (!ConditionExecutor.testItem(itemCondition, stack)) return;
				if (!isActive(p)) return;
				FoodComponent food = Utils.parseProperties(foodComponent);
				float s = food.getEatSeconds();
				Arrays.stream(modifiers).forEach(modifier -> {
					Utils.getOperationMappingsFloat().get(modifier.operation()).apply(s, modifier.value());
				});
				food.setEatSeconds(s);
				ItemMeta meta = stack.getItemMeta();
				meta.getPersistentDataContainer().set(GenesisMC.apoliIdentifier("edible_item_modified"), PersistentDataType.BOOLEAN, true);
				meta.setFood(food);
				stack.setItemMeta(meta);
				return;
			}

			if (stack.getItemMeta().getPersistentDataContainer().has(GenesisMC.apoliIdentifier("edible_item_modified"))) {
				ItemMeta meta = stack.getItemMeta();
				meta.setFood(null);
				meta.getPersistentDataContainer().remove(GenesisMC.apoliIdentifier("edible_item_modified"));
				stack.setItemMeta(meta);
			}
		}
	}

}
