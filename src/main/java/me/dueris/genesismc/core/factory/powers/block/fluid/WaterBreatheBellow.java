package me.dueris.genesismc.core.factory.powers.block.fluid;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.water_breathing;

public class WaterBreatheBellow extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(water_breathing.contains(OriginPlayer.getOriginTag(p))){

            }
        }

    }
}
