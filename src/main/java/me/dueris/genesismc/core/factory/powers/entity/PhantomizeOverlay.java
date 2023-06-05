package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

import static me.dueris.genesismc.core.factory.powers.Powers.phantomize;
import static me.dueris.genesismc.core.factory.powers.Powers.phantomize_overlay;

public class PhantomizeOverlay extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {

            PersistentDataContainer data = p.getPersistentDataContainer();
            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);

            if (phantomid == 2) {
                if (phantomize_overlay.contains(p.getUniqueId().toString())) {
                    Phantomized.initializePhantomOverlay(p);
                }else{Phantomized.deactivatePhantomOverlay(p);}
            }else{Phantomized.deactivatePhantomOverlay(p);}
        }
    }
}
