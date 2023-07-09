package me.dueris.genesismc.core.factory.powers.world;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.burn_in_daylight;

public class BurnInDaylight extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            boolean phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN);
            if (burn_in_daylight.contains(p) && phantomid != true) {
                if ((p.getLocation().getBlockY() + 1 > p.getWorld().getHighestBlockYAt(p.getLocation()))) {
                    if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
                        if (p.getWorld().isDayTime() && !p.isInWaterOrRainOrBubbleColumn()) {
                            p.setFireTicks(80);
                        }
                    }
                }
            }
        }
    }
}
