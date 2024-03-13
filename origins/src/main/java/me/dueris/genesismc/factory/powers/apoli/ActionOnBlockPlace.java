package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnBlockPlace extends CraftPower implements Listener {

	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void blockBreak(BlockPlaceEvent e) {
		if (action_on_block_place.contains(e.getPlayer())) {
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
					if (!(ConditionExecutor.testEntity((JSONObject) power.get("condition"), (CraftEntity) e.getPlayer()) && ConditionExecutor.testItem((JSONObject) power.get("item_condition"), e.getItemInHand()) && ConditionExecutor.testBlock((JSONObject) power.get("place_to_condition"), (CraftBlock) e.getBlockPlaced()) && ConditionExecutor.testBlock((JSONObject) power.get("place_on_condition"), (CraftBlock) e.getBlockAgainst())))
						return;
					e.setCancelled(true);
					setActive(e.getPlayer(), power.getTag(), true);
					Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
					Actions.ItemActionType(e.getItemInHand(), power.getAction("held_item_action"));
					Actions.BlockActionType(e.getBlockAgainst().getLocation(), power.getAction("place_on_action"));
					Actions.BlockActionType(e.getBlockPlaced().getLocation(), power.getAction("place_to_action"));
					if (power.get("result_stack") != null) {
						JSONObject jsonObject = power.get("result_stack");
						int amt;
						if (jsonObject.get("amount").toString() != null) {
							amt = Integer.parseInt(jsonObject.get("amount").toString());
						} else {
							amt = 1;
						}
						ItemStack itemStack = new ItemStack(Material.valueOf(jsonObject.get("item").toString().toUpperCase().split(":")[jsonObject.get("item").toString().split(":").length]), amt);
						e.getPlayer().getInventory().addItem(itemStack);
						Actions.ItemActionType(itemStack, power.getAction("result_item_action"));
					}
				}
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:action_on_block_place";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return action_on_block_place;
	}

}
