package me.dueris.genesismc.core.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_sleep;
import static me.dueris.genesismc.core.factory.powers.entity.FreshAir.beds;

public class PreventSleep implements Listener {
    @EventHandler
    public void run(PlayerInteractEvent e){
        if(e.getClickedBlock() == null) return;
        if(beds.contains(e.getClickedBlock().getType())){
            if(!prevent_sleep.contains(e.getPlayer())) return;
            for(OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()){
                if(ConditionExecutor.check("block_condition", "block_conditions", e.getPlayer(), origin, "origins:prevent_sleep", null, e.getPlayer())){
                    if(origin.getPowerFileFromType("origins:prevent_sleep").get("set_spawn_point", "false").toString() == "true"){
                        e.getPlayer().setBedSpawnLocation(e.getClickedBlock().getLocation());
                    }
                    if(origin.getPowerFileFromType("origins:prevent_sleep").get("message", "origins.cant_sleep") != null){
                        e.getPlayer().sendMessage(origin.getPowerFileFromType("origins:prevent_sleep").get("message", "origins.cant_sleep"));
                    }
                    e.setCancelled(true);
                }
            }
        }
    }
}
