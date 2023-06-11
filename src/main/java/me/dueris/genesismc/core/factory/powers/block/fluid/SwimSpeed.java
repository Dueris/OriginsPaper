package me.dueris.genesismc.core.factory.powers.block.fluid;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.swim_speed;

public class SwimSpeed extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(swim_speed.contains(p)){
                if(!p.isInWaterOrBubbleColumn()) return;
                p.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 15, 3, false, false, false));
            }
        }
    }
}
