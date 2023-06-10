package me.dueris.genesismc.core.factory.powers.entity;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.dueris.genesismc.core.factory.powers.Powers.projectile_immune;

public class ProjectileImmune implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player || e.getEntity() instanceof HumanEntity) {
            Player p = (Player) e.getEntity();
            if (projectile_immune.contains(p)) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                    e.setDamage(0);
                    e.setCancelled(true);
                }//hi

            }
        }
    }

}
