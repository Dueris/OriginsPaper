package me.dueris.genesismc.utils;

import io.papermc.paper.entity.TeleportFlag;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.OriginCommandSender;
import me.dueris.genesismc.OriginDataContainer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
        Player p = e.getPlayer();
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
                    if(i[0] > 20) cancel();
                    p.teleportAsync(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
                    i[0]++;
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
        }
    }

    public String formatLocation(Location location){
        return location.x() + "//" + location.getY() + "//" + location.getZ() + "//" + location.getYaw() + "//" + location.getPitch();
    }
}
