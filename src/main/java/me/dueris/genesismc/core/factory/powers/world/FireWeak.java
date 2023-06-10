package me.dueris.genesismc.core.factory.powers.world;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.fire_weak;
import static org.bukkit.Material.SOUL_FIRE;

public class FireWeak extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (fire_weak.contains(p)) {
                Location location = p.getLocation();
                for (int x = (int) (location.getX() - 4); x < location.getX() + 4; x++) {
                    for (int y = (int) (location.getY() - 2); y < location.getY() + 2; y++) {
                        for (int z = (int) (location.getZ() - 4); z < location.getZ() + 4; z++) {
                            if (p.getWorld().getBlockAt(x, y, z).getType() == SOUL_FIRE) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 1, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 10, 1, false, false, false));
                            }
                        }
                    }
                }
            }
        }
    }
}
