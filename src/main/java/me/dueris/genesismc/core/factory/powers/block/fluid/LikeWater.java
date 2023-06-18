package me.dueris.genesismc.core.factory.powers.block.fluid;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.dueris.genesismc.core.factory.powers.Powers.like_water;

public class LikeWater extends BukkitRunnable implements Listener {
    ArrayList<Player> toggled_players = new ArrayList<>();

//    @EventHandler
//    public void ToggleLikeWater(PlayerToggleSneakEvent e){
//        if(like_water.contains(e.getPlayer())){
//            Player p = e.getPlayer();
//            while (p.isInWater()){
//                if(toggled_players.contains(p)){
//                    p.setFlying(false);
//                    toggled_players.remove(p);
//                }else{
//                    p.setFlying(true);
//                    toggled_players.add(p);
//                }
//            }
//        }
//    }

    @Override
    public void run() {
//        for(Player p : Bukkit.getOnlinePlayers()){
//            if(p.getGameMode().equals(GameMode.SPECTATOR) || p.getGameMode().equals(GameMode.CREATIVE)){
//                p.setAllowFlight(true);
//            }else{
//                if(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)){
//                    if(like_water.contains(p)){
//                        if(toggled_players.contains(p)){
//                            p.setFlying(true);
//                            p.setAllowFlight(false);
//                        }else{
//                            p.setFlying(false);
//                            p.setAllowFlight(false);
//                        }
//                    }
//                }else{
//                    p.setFlying(false);
//                    p.setAllowFlight(false);
//                }
//            }
//        }
    }
}
