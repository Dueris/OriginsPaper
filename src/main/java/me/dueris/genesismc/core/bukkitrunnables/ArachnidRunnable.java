package me.dueris.genesismc.core.bukkitrunnables;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Material.AIR;

public class ArachnidRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 1709012) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 1, false, false, false));
            }
            if (originid == 1709012) {
                if (p.getLocation().getBlock().getRelative(BlockFace.EAST).getType() != AIR ||
                        p.getLocation().getBlock().getRelative(BlockFace.WEST).getType() != AIR ||
                        p.getLocation().getBlock().getRelative(BlockFace.NORTH).getType() != AIR ||
                        p.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType() != AIR ||
                        p.getEyeLocation().add(0, 1, 0).getBlock().getType() != AIR ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType() != AIR ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.WEST).getType() != AIR ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.NORTH).getType() != AIR ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.SOUTH).getType() != AIR) {
                    Block block = p.getTargetBlock(null, 2);
                    Location bl = block.getLocation();

                    if (block.getType() != AIR && p.isSneaking() && !p.isInRain()) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 5, 1, false, false, false));
                    }else{
                        if (!p.isInRain()) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 3, false, false, false));
                        }
                    }
                }
            }
        }
    }
}
