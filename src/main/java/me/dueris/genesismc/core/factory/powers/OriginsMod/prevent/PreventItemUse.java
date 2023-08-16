package me.dueris.genesismc.core.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_item_use;

public class PreventItemUse implements Listener {
    @EventHandler
    public void run(PlayerInteractEvent e){
        if(prevent_item_use.contains(e.getPlayer())){
            for(OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()){
                if(ConditionExecutor.check("item_condition", e.getPlayer(), origin, "origins:prevent_item_use", null, e.getPlayer())){
                    e.setCancelled(true);
                }
            }
        }
    }
}
