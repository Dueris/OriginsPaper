package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import java.util.ArrayList;

public class ModifyCraftingPower extends CraftPower implements Listener {

    @EventHandler
    public void runD(PrepareItemCraftEvent e) {
	Player p = (Player) e.getInventory().getHolder();
	if (modify_crafting.contains(p)) {
	    if (e.getRecipe() == null) return;
	    if (e.getInventory().getResult() == null) return;
	    for (Layer layer : CraftApoli.getLayersFromRegistry()) {
		for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
		    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
			String currKey = RecipePower.computeTag(e.getRecipe());
			if (currKey == null) continue;
			String provKey = power.getStringOrDefault("recipe", currKey);
			boolean set = false;
			if (currKey.equals(provKey)) { // Matched on crafting
			    set = ConditionExecutor.testItem(power.getJsonObject("item_condition"), e.getInventory().getResult());
			}
			if (set) {
			    if (power.isPresent("result")) {
				e.getInventory().setResult(RecipePower.computeResult(power.getJsonObject("result")));
			    }
			    Actions.executeEntity(p, power.getJsonObject("entity_action"));
			    Actions.executeItem(e.getInventory().getResult(), power.getJsonObject("item_action"));
			    Actions.executeBlock(p.getLocation(), power.getJsonObject("block_action"));
			}
		    } else {
			setActive(p, power.getTag(), false);
		    }
		}
	    }
	}
    }

    @Override
    public String getType() {
	return "apoli:modify_crafting";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
	return modify_crafting;
    }
}
