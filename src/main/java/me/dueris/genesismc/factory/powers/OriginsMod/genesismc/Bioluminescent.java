package me.dueris.genesismc.factory.powers.OriginsMod.genesismc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.factory.powers.Power.bioluminescent;

public class Bioluminescent extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!bioluminescent.contains(p)) return;
//            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
//                Block block = p.getLocation().getBlock();
//                block.getState().update(true, false);
//                for(Player player : Bukkit.getOnlinePlayers()){
//                    GlowAPI.setGlowing(p, GlowAPI.Color.RED, p);
//                }
//            }
            //DO NOT UNCOMMENT THIS CODE BROKE RENDERING SO BADLY LOL
        }
    }
}
