package me.purplewolfmc.genesismc.core.bukkitrunnables;

import me.purplewolfmc.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class CreepRunnable extends BukkitRunnable {

    private final HashMap<UUID, Long> cooldown;

    public CreepRunnable() {
        this.cooldown = new HashMap<>();
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 1407068) {
                p.setHealthScale(20);
                p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
            }
        }
    }
}
