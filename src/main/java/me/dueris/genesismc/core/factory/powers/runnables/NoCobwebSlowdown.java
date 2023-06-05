package me.dueris.genesismc.core.factory.powers.runnables;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.no_cobweb_slowdown;

public class NoCobwebSlowdown implements Listener {

    @EventHandler
    public void NoCobwebSlowdown(PlayerMoveEvent e) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (no_cobweb_slowdown.contains(p)) {
                Location location = p.getLocation();
                if (location.getBlock().getType() == Material.COBWEB) {
                    p.sendBlockChange(location, Material.AIR.createBlockData());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.sendBlockChange(location, Material.COBWEB.createBlockData());
                        }
                    }.runTaskLater(GenesisMC.getPlugin(), 100L);
                }
            }
        }
    }
}
