package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
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
        if (fall_immunity.contains(OriginPlayer.getOriginTag(p))) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;

        if (resist_fall.contains(OriginPlayer.getOriginTag(p))) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setDamage(e.getDamage() - 4);
            }
        }
    }


}
