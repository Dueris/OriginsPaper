package me.dueris.genesismc.core.factory.powers.entity;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import static me.dueris.genesismc.core.factory.powers.Powers.felinephobia;

public class CreeperIgnore implements Listener {

    @EventHandler
    public void OnTarget(EntityTargetEvent e) {
        if (e.getEntity() instanceof Creeper && (e.getTarget() instanceof Player p)) {

            if (felinephobia.contains(p)) {
                e.setCancelled(true);
            }
        }
    }

}
