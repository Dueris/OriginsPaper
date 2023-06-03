package me.dueris.genesismc.core.factory.powers.runnables;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.slow_falling;

public class SlowFalling extends BukkitRunnable {

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (slow_falling.contains(OriginPlayer.getOrigin(p).getTag())) {
                if (!p.isGliding() && !p.isSneaking() && !p.isSleeping() && !p.isDeeplySleeping() && !(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 10, 0, false, false, false));
                }
            }
        }
    }

}
