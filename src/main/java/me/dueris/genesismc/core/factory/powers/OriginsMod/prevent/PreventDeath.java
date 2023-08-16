package me.dueris.genesismc.core.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_death;

public class PreventDeath implements Listener {
    @EventHandler
    public void run(PlayerDeathEvent e){
        if(prevent_death.contains(e.getPlayer())){
            for(OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()){
                if(ConditionExecutor.check("damage_condition", e.getPlayer(), origin, "origins:prevent_death", e.getPlayer().getLastDamageCause(), e.getPlayer())){
                    e.setCancelled(true);
                }
            }
        }
    }
}
