package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_block_use;

public class PreventBlockUse extends CraftPower implements Listener {

    @EventHandler
    public void run(PlayerInteractEvent e) {
	if (e.getClickedBlock() == null) return;
	if (prevent_block_use.contains(e.getPlayer())) {
	    for (Layer layer : CraftApoli.getLayersFromRegistry()) {
		for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getType(), layer)) {
		    if (ConditionExecutor.testBlock(power.getJsonObject("block_condition"), (CraftBlock) e.getClickedBlock()) && ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) e.getPlayer())) {
			e.setCancelled(true);
		    }
		}
	    }
	}
    }

    @Override
    public String getType() {
	return "apoli:prevent_block_use";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
	return prevent_block_use;
    }
}
