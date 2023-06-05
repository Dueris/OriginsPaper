package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.dueris.genesismc.core.factory.powers.Powers.decreased_explosion;

public class DecreaseExplosion implements Listener {
    @EventHandler
    public void onCreepDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (decreased_explosion.contains(p.getUniqueId().toString())) {
                if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                    e.setDamage(e.getFinalDamage() * 0.55);
                }
            }
        }
    }
}
