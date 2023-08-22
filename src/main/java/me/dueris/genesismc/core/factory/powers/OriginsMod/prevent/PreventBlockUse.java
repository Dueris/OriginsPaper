package me.dueris.genesismc.core.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_block_selection;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_block_use;

public class PreventBlockUse implements Listener {
    @EventHandler
    public void run(PlayerInteractEvent e){
        if(prevent_block_use.contains(e.getPlayer())){
            for(OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(conditionExecutor.check("block_condition", "block_conditions", e.getPlayer(), origin, "origins:prevent_block_used", null, e.getPlayer())){
                    if(e.getClickedBlock() != null) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void run(BlockPlaceEvent e){
        if(prevent_block_use.contains(e.getPlayer())){
            for(OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(conditionExecutor.check("block_condition", "block_conditions", e.getPlayer(), origin, "origins:prevent_block_used", null, e.getPlayer())){
                    e.setCancelled(true);
                }
            }
        }
    }
}
