package me.dueris.genesismc.core.factory.powers.world;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import static org.bukkit.Material.SOUL_FIRE;

public class FireWeak extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if (origintag.equalsIgnoreCase("genesis:origin-piglin")) {
                if (p.getWorld().getEnvironment() != World.Environment.NETHER) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10, 0, false, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 0, false, false, false));
                }

                Location location = p.getLocation();
                for (int x = (int) (location.getX()-4); x < location.getX()+4; x++) {
                    for (int y = (int) (location.getY()-2); y < location.getY()+2; y++) {
                        for (int z = (int) (location.getZ()-4); z < location.getZ()+4; z++) {
                            if (p.getWorld().getBlockAt(x,y,z).getType() == SOUL_FIRE) {
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
