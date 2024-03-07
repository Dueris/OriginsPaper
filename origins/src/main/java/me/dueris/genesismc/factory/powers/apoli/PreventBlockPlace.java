package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_block_place;

public class PreventBlockPlace extends CraftPower implements Listener {

	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void blockBreak(BlockPlaceEvent e) {
		if (prevent_block_place.contains(e.getPlayer())) {
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
				}
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:prevent_block_place";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return prevent_block_place;
	}

	@Override
	public void setActive(Player p, String tag, Boolean bool) {
		if (powers_active.containsKey(p)) {
			if (powers_active.get(p).containsKey(tag)) {
				powers_active.get(p).replace(tag, bool);
			} else {
				powers_active.get(p).put(tag, bool);
			}
		} else {
			powers_active.put(p, new HashMap());
			setActive(p, tag, bool);
		}
	}
}
