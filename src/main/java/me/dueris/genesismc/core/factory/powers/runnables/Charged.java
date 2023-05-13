package me.dueris.genesismc.core.factory.powers.runnables;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.factory.powers.Powers.charged;

public class Charged extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if (charged.contains(origintag)) {

                if (p.getWorld().isThundering()) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20, 0, true, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 2, true, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 2, true, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 1, true, false, false));

                }
            }
        }
    }
}
