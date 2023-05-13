package me.dueris.genesismc.core.factory.powers.runnables;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.factory.powers.Powers.burn_in_daylight;

public class BurnInDaylight extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
            if (burn_in_daylight.contains(origintag) && phantomid != 2) {
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
