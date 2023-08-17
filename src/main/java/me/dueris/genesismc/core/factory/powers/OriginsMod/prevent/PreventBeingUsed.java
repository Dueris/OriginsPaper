package me.dueris.genesismc.core.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_being_used;

public class PreventBeingUsed implements Listener {
    @EventHandler
    public void run(PlayerInteractEvent e){
        if(prevent_being_used.contains(e.getPlayer())){
            Player p = e.getPlayer();
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                if(ConditionExecutor.check("bientity_condition", "bientity_conditions", p, origin, "origins:prevent_being_used", null, p)){
                    if(ConditionExecutor.check("item_condition", "item_conditions", p, origin, "origins:prevent_being_used", null, p)){
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
