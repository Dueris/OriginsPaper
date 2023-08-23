package me.dueris.genesismc.factory.powers.genesis;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.dueris.genesismc.factory.powers.Power.decreased_explosion;

public class DecreaseExplosion implements Listener {
    @EventHandler
    public void onCreepDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (decreased_explosion.contains(p)) {
                if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                    e.setDamage(e.getFinalDamage() * 0.55);
                }
            }
        }
    }
}
