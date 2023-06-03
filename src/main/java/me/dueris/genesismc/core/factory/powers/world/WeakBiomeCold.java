package me.dueris.genesismc.core.factory.powers.world;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.weak_biome_cold;

public class WeakBiomeCold extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Location location = p.getLocation();
            if (weak_biome_cold.contains(OriginPlayer.getOrigin(p).getTag())) {
                if (location.getBlock().getTemperature() < 0.6) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 1, false, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 10, 1, false, false, false));
                }
            }
        }
    }
}
