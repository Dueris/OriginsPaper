package me.dueris.genesismc.core.factory.powers.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static me.dueris.genesismc.core.factory.powers.Powers.felinephobia;
import static me.dueris.genesismc.core.factory.powers.Powers.scare_creepers;

public class FelinePhobia extends BukkitRunnable implements Listener {

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (felinephobia.contains(p)) {
                List<Entity> nearby2 = p.getNearbyEntities(3, 3, 3);
                for (Entity tmp : nearby2)
                    if (tmp instanceof Cat)
                        p.damage(1);
            }
        }

    }
}
