package me.dueris.genesismc.core.factory.powers.entity;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import static me.dueris.genesismc.core.factory.powers.Powers.scare_creepers;

public class CreeperScare implements Listener {
    @EventHandler
    public void OnTarget(EntityTargetEvent e) {
        if (e.getEntity() instanceof Creeper && (e.getTarget() instanceof Player p)) {
            if (scare_creepers.contains(p)) {
                e.setCancelled(true);
            }
        }
    }

}
