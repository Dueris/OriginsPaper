package me.dueris.genesismc.factory.powers.effects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.factory.powers.OriginsMod.block.WaterBreathe.isInBreathableWater;
import static me.dueris.genesismc.factory.powers.Power.aqua_affinity;

public class AquaAffinity extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (aqua_affinity.contains(p)) {
                if (isInBreathableWater(p)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 15, 2, false, false, false));
                }
            }
        }
    }
}
