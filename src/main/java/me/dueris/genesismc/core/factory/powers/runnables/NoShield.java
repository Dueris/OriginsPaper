package me.dueris.genesismc.core.factory.powers.runnables;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.no_shield;
import static org.bukkit.Material.SHIELD;

public class NoShield extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (no_shield.contains(OriginPlayer.getOriginTag(p))) {
                p.setCooldown(SHIELD, 100);
            }
        }
    }
}
