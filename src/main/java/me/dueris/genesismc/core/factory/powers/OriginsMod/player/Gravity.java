package me.dueris.genesismc.core.factory.powers.OriginsMod.player;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static me.dueris.genesismc.core.factory.powers.Powers.no_gravity;
import static me.dueris.genesismc.core.factory.powers.Powers.particle;

public class Gravity extends BukkitRunnable implements Listener {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(no_gravity.contains(p)){
                p.setGravity(false);
                p.setFallDistance(0.1f);
            }else{
                p.setGravity(true);
            }

        }
    }

    @EventHandler
    public void shiftgodown(PlayerToggleSneakEvent e){
        if(no_gravity.contains(e.getPlayer())){
            if(e.getPlayer().isOnGround()) return;
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(e.getPlayer().isSneaking()){
                        e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), e.getPlayer().getVelocity().getY() - 0.1, e.getPlayer().getVelocity().getZ()));
                    }else{
                        this.cancel();
                    }

                }
            }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
        }

    }

    @EventHandler
    public void jumpyupy(PlayerJumpEvent e) {
        if(no_gravity.contains(e.getPlayer())){
            e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), e.getPlayer().getVelocity().getY() + 1, e.getPlayer().getVelocity().getZ()));
        }
    }
}
