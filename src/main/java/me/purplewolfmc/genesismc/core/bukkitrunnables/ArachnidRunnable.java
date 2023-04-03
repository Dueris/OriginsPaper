package me.purplewolfmc.genesismc.core.bukkitrunnables;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ArachnidRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.getScoreboardTags().contains("arachnid")){
                p.setMaxHealth(14);
                p.setHealthScale(14);
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 1, false, false, false));
            }
        }
    }
}
