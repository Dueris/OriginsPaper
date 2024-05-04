package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class StartingEquipmentPower extends CraftPower implements Listener {

    @EventHandler
    public void runGive(OriginChangeEvent e) {
	if (starting_equip.contains(e.getPlayer())) {
	    for (Layer layer : CraftApoli.getLayersFromRegistry()) {
		for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getType(), layer)) {
		    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) e.getPlayer())) {
			setActive(e.getPlayer(), power.getTag(), true);
			runGiveItems(e.getPlayer(), power);
		    } else {
			setActive(e.getPlayer(), power.getTag(), false);
		    }
		}
	    }
	}
    }

    public void runGiveItems(Player p, Power power) {
	for (FactoryJsonObject stack : power.getList$SingularPlural("stack", "stacks").stream().map(FactoryElement::toJsonObject).toList()) {
	    p.getInventory().addItem(new ItemStack(Material.valueOf(stack.getString("item").toUpperCase().split(":")[1]), power.getNumberOrDefault("amount", 1).getInt()));
	}
    }

    @EventHandler
    public void runRespawn(PlayerRespawnEvent e) {
	if (starting_equip.contains(e.getPlayer())) {
	    for (Layer layer : CraftApoli.getLayersFromRegistry()) {
		for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getType(), layer)) {
		    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) e.getPlayer())) {
			setActive(e.getPlayer(), power.getTag(), true);
			if (power.isPresent("recurrent") && power.getBoolean("recurrent")) {
			    runGiveItems(e.getPlayer(), power);
			}
		    } else {
			setActive(e.getPlayer(), power.getTag(), false);
		    }
		}
	    }
	}
    }

    @Override
    public String getType() {
	return "apoli:starting_equipment";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
	return starting_equip;
    }
}
