package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class CreeperIgnore implements Listener {

    @EventHandler
    public void OnTarget(EntityTargetEvent e) {
        if (e.getEntity() instanceof Creeper && (e.getTarget() instanceof Player)) {

            Player p = (Player) e.getTarget();
            if (OriginPlayer.getOriginTag(p).equalsIgnoreCase("genesis:origin-creep")) {
                e.setCancelled(true);
            }
        }
    }

}
