package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.factory.powers.Powers.hotblooded;

public class HotBlooded extends BukkitRunnable implements Listener {

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (hotblooded.contains(p)) {
                if (p.hasPotionEffect((PotionEffectType.POISON))) {
                    p.removePotionEffect(PotionEffectType.POISON);
                }
                if (p.hasPotionEffect(PotionEffectType.POISON)) {
                    p.removePotionEffect(PotionEffectType.HUNGER);
                }
            }
        }
    }
}
