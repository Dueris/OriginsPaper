package me.dueris.genesismc.core.factory.powers.effects;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.overworld_piglin_zombified;

public class OverworldPiglinZombified extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (overworld_piglin_zombified.contains(p)) {
                if (p.getWorld().getEnvironment() != World.Environment.NETHER) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 50, 0, false, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 0, false, false, false));
                }
            }
        }
    }
}
