package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class EdibleItem extends CraftPower implements Listener {
	public static void runResultStack(Power power, boolean runActionUpon, InventoryHolder holder) {
		FactoryJsonObject stack = power.getJsonObject("result_stack");
		int amt;
		if (stack.isPresent("amount")) {
			amt = stack.getNumber("amount").getInt();
		} else {
			amt = 1;
		}
		ItemStack itemStack = new ItemStack(power.getMaterial(stack.getString("item")), amt);
		holder.getInventory().addItem(itemStack);
		if (runActionUpon) Actions.executeItem(itemStack, power.getJsonObject("result_item_action"));
	}

	@EventHandler
	public void actions(PlayerItemConsumeEvent e) {
		if (e.getItem() != null && getPlayersWithPower().contains(e.getPlayer())) {
			for (Power power : OriginPlayerAccessor.getPowers(e.getPlayer(), getType())) {
				if (!ConditionExecutor.testItem(power.getJsonObject("item_condition"), e.getItem())) continue;
				if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) e.getPlayer()))
					continue;
				new BukkitRunnable() {
					@Override
					public void run() {
						if (power.isPresent("result_stack")) {
							runResultStack(power, power.isPresent("result_item_action"), e.getPlayer());
						}
						Actions.executeEntity(e.getPlayer(), power.getJsonObject("entity_action"));
						Actions.executeItem(e.getItem(), power.getJsonObject("item_action"));
					}
				}.runTaskLater(GenesisMC.getPlugin(), 1);
			}
		}
	}

	@EventHandler
	public void setFoodable(PlayerItemHeldEvent e) {
		ItemStack stack = e.getPlayer().getInventory().getItem(e.getNewSlot());
		if (stack != null) {
			if (getPlayersWithPower().contains(e.getPlayer()) && !stack.getItemMeta().getPersistentDataContainer().has(GenesisMC.apoliIdentifier("edible_item_modified"))) {
				Player p = e.getPlayer();
				for (Power power : OriginPlayerAccessor.getPowers(p, getType())) {
					if (!ConditionExecutor.testItem(power.getJsonObject("item_condition"), stack)) continue;
					if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) continue;
					FoodComponent food = Utils.parseProperties(power.getJsonObject("food_component"));
					float s = food.getEatSeconds();
					power.getList$SingularPlural("consuming_time_modifier", "consuming_time_modifiers").forEach(jO -> {
						Modifier modifier = new Modifier(jO.toJsonObject());
						Utils.getOperationMappingsFloat().get(modifier.operation()).apply(s, modifier.value());
					});
					food.setEatSeconds(s);
					ItemMeta meta = stack.getItemMeta();
					meta.getPersistentDataContainer().set(GenesisMC.apoliIdentifier("edible_item_modified"), PersistentDataType.BOOLEAN, true);
					meta.setFood(food);
					stack.setItemMeta(meta);
				}
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

	@Override
	public String getType() {
		return "apoli:edible_item";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return edible_item;
	}
}
