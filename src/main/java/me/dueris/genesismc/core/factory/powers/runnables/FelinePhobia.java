package me.dueris.genesismc.core.factory.powers.runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static me.dueris.genesismc.core.factory.powers.Powers.felinephobia;

public class FelinePhobia extends BukkitRunnable {
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
