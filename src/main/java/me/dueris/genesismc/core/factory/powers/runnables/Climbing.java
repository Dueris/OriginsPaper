package me.dueris.genesismc.core.factory.powers.runnables;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.climbing;
import static org.bukkit.Material.AIR;

public class Climbing extends BukkitRunnable {

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (climbing.contains(p)) {
                if (p.getLocation().getBlock().getRelative(BlockFace.EAST).getType().isSolid() ||
                        p.getLocation().getBlock().getRelative(BlockFace.WEST).getType().isSolid() ||
                        p.getLocation().getBlock().getRelative(BlockFace.NORTH).getType().isSolid() ||
                        p.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isSolid() ||
                        p.getEyeLocation().add(0, 1, 0).getBlock().getType().isSolid() ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType().isSolid() ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.WEST).getType().isSolid() ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.NORTH).getType().isSolid() ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isSolid()) {
                    Block block = p.getTargetBlock(null, 2);

                    if (block.getType() != AIR && p.isSneaking() && !p.isInRain()) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 6, 2, false, false, false));
                    }
                }
            }
        }
    }
}
