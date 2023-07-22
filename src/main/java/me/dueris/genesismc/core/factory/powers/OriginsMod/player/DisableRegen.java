package me.dueris.genesismc.core.factory.powers.OriginsMod.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.BatchUpdateException;

import static me.dueris.genesismc.core.factory.powers.Powers.disable_regen;

public class DisableRegen implements Listener {
    @EventHandler
    public void disable(EntityRegainHealthEvent e){
        if(e.getEntity() instanceof Player p){
            if(disable_regen.contains(p)){
                if(e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)){
                    e.setAmount(0);
                    e.setCancelled(true);
                }
            }
        }
    }
}
