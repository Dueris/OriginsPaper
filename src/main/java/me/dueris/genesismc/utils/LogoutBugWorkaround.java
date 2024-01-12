package me.dueris.genesismc.utils;

import me.dueris.genesismc.GenesisMC;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class LogoutBugWorkaround implements Listener {
    @EventHandler
    public void logout(PlayerQuitEvent e){
        Player p = e.getPlayer();
        p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "logoutWorkaroundLocation"), PersistentDataType.STRING, this.formatLocation(p.getLocation()));
    }

    @EventHandler
    public void login(PlayerJoinEvent e){
        freezeLogin(e.getPlayer());
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e){
        final int[] i = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if(i[0] > 15) cancel();
                e.getPlayer().teleportAsync(e.getPlayer().getLocation());
                i[0]++;
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
    }

    public void freezeLogin(Player p){
        if(p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "logoutWorkaroundLocation"), PersistentDataType.STRING)){
            String logoutData = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "logoutWorkaroundLocation"), PersistentDataType.STRING);
            String[] splitData = logoutData.split("//");
            Location location = new Location(
                    p.getWorld(),
                    Double.valueOf(splitData[0]),
                    Double.valueOf(splitData[1]),
                    Double.valueOf(splitData[2]),
                    Float.valueOf(splitData[3]),
                    Float.valueOf(splitData[4])
            );
            final int[] i = {0};
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(i[0] > 15) cancel();
                    p.teleportAsync(location);
                    i[0]++;
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
        }
    }

    public String formatLocation(Location location){
        return location.x() + "//" + location.getY() + "//" + location.getZ() + "//" + location.getYaw() + "//" + location.getPitch();
    }
}
