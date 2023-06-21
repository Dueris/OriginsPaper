package me.dueris.genesismc.core.origins;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class OriginHandler implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player || e.getEntity() instanceof HumanEntity) {
            Player p = (Player) e.getEntity();
            // old genesis code
//            if (OriginPlayer.getOrigin(p).getTag().equalsIgnoreCase("genesis:origin-enderian")) {
//                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 10, 1F);
//            }
        }
    }

    @EventHandler
    public void onTargetShulk(EntityTargetEvent e) {
        if (e.getEntity() instanceof ShulkerBullet) {
            if (e.getTarget() instanceof Player p) {
                // old genesis code
//                if (OriginPlayer.getOrigin(p).getTag().equalsIgnoreCase("genesis:origin-shulk")) {
//                    if (e.getTarget() instanceof Player) {
//                        e.setCancelled(true);
//                    }
//                }
            }
        }
    }

}
