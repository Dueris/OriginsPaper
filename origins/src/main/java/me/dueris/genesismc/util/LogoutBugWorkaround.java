package me.dueris.genesismc.util;

import me.dueris.genesismc.GenesisMC;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class LogoutBugWorkaround implements Listener {
    @EventHandler
    public void logout(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "logoutWorkaroundLocation"), PersistentDataType.STRING, this.formatLocation(p.getLocation()));
    }

    @EventHandler
    public void login(PlayerJoinEvent e) {
        if (!e.getPlayer().getLocation().getChunk().isLoaded()) {
            e.getPlayer().getLocation().getChunk().load(true);
        }
        freezeLogin(e.getPlayer());
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent e) {
        if (!e.getTo().getChunk().isLoaded()) {
            e.getTo().getChunk().load(true);
        }
    }

    public void freezeLogin(Player p) {
        if (p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "logoutWorkaroundLocation"), PersistentDataType.STRING)) {
            String logoutData = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "logoutWorkaroundLocation"), PersistentDataType.STRING);
            String[] splitData = logoutData.split("//");
            Location location = new Location(
                p.getWorld(),
                Double.parseDouble(splitData[0]),
                Double.parseDouble(splitData[1]),
                Double.parseDouble(splitData[2]),
                Float.parseFloat(splitData[3]),
                Float.parseFloat(splitData[4])
            );
            final int[] i = {0};
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (i[0] > 15) cancel();
                    p.teleportAsync(location);
                    i[0]++;
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
        }
    }

    @EventHandler
    public void endPlatformFix(PlayerTeleportEvent e) { // Fixes spawning inside the platform, making the player fall into the void if the platform is floating
        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            e.setTo(new Location(e.getTo().getWorld(), e.getTo().getX(), 51, e.getTo().getZ(), e.getTo().getYaw(), e.getTo().getPitch()));
        }
    }

    public String formatLocation(Location location) {
        return location.x() + "//" + location.getY() + "//" + location.getZ() + "//" + location.getYaw() + "//" + location.getPitch();
    }
}
