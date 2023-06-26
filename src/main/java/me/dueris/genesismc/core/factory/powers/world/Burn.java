package me.dueris.genesismc.core.factory.powers.world;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.burn;

public class Burn extends BukkitRunnable {

    private Long interval;

    private int ticksE;

    public Burn() {
        this.interval = 1L;
        this.ticksE = 0;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()){
            if(burn.contains(p)){
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    PowerContainer power = origin.getPowerFileFromType("origins:burn");
                    if (power == null) continue;
                    if(power.getInterval() == null) {Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Unable to parse interval for origins:burn"); return;}
                    interval = power.getInterval();
                if(ticksE < interval) {
                    ticksE++;
                    return;
                }else{
                    Long burn_duration = power.getBurnDuration();
                    p.setFireTicks(burn_duration.intValue());

                    ticksE = 0;
                }
                }
            }
        }
    }
}
