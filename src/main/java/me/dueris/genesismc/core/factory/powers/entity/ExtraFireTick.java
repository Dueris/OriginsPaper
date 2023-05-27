package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.dueris.genesismc.core.factory.powers.Powers.extra_fire;

public class ExtraFireTick implements Listener {
    @EventHandler
    public void onFireDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (extra_fire.contains(OriginPlayer.getOriginTag(p))) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || e.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
                    e.setDamage(e.getDamage() * 1.5);
                }
            }
        }
    }

}
