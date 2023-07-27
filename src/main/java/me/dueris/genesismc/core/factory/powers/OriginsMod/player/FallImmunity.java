package me.dueris.genesismc.core.factory.powers.OriginsMod.player;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.dueris.genesismc.core.factory.powers.Powers.fall_immunity;
import static me.dueris.genesismc.core.factory.powers.Powers.resist_fall;

public class FallImmunity implements Listener {

    @EventHandler
    public void acrobatics(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (fall_immunity.contains(p)) {
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                if(ConditionExecutor.check(p, origin, "origins:fall_immunity", e, p)){
                    if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
