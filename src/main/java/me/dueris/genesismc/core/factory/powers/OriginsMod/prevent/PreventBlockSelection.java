package me.dueris.genesismc.core.factory.powers.OriginsMod.prevent;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_block_selection;

public class PreventBlockSelection implements Listener {
    @EventHandler
    public void run(PlayerInteractEvent e){
        if(prevent_block_selection.contains(e.getPlayer())){
            for(OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()){
                if(ConditionExecutor.check("block_condition", "block_condition", e.getPlayer(), origin, "origins:prevent_block_selection", null, e.getPlayer())){
                    if(e.getClickedBlock() != null) e.setCancelled(true);
                }
            }
        }
    }
}
