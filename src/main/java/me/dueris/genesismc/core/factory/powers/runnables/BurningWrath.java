package me.dueris.genesismc.core.factory.powers.runnables;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.burning_wrath;

public class BurningWrath extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (burning_wrath.contains(p.getUniqueId().toString())) {
                if (p.getFireTicks() > 0) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10, 0, false, false, false));
                }
            }
        }
    }
}
