package me.dueris.genesismc;

import me.dueris.genesismc.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class OriginDataContainer {
    private static final HashMap<Player, String> dataContainer = new HashMap<>();

    public static void loadData() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            OriginDataContainer.loadData(p);
        }
    }

    public static void loadData(Player player) {
        dataContainer.put(
                player,
                player.getPersistentDataContainer().get(
                    GenesisMC.identifier("originLayer"),
                        PersistentDataType.STRING
                )
        );
    }

    public static String getLayer(Player player) {
        return dataContainer.get(player);
    }

    public static void unloadAllData() {
        dataContainer.clear();
    }

    public static void unloadData(Player player) {
        dataContainer.remove(player);
    }

    public static HashMap<Player, String> getDataMap() {
        return dataContainer;
    }

    public static void runTickUpdate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                OriginDataContainer.unloadAllData();
                OriginDataContainer.loadData();
            }
        }.runTaskLater(
                GenesisMC.getPlugin(),
                GenesisDataFiles.getMainConfig().getLong("tickRate"));
    }
}
