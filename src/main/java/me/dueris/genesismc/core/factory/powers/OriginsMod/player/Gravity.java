package me.dueris.genesismc.core.factory.powers.OriginsMod.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.no_gravity;

public class Gravity extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(no_gravity.contains(p)){
                p.setGravity(false);
            }else{
                p.setGravity(true);
            }
        }
    }
}
