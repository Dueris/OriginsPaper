package me.dueris.genesismc.core.factory.powers.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.aquatic;

public class ImpalingMore implements Listener {

    @EventHandler
    public void ImpalingEvent(EntityDamageByEntityEvent e){
        if(!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if(e.getDamager() instanceof Trident){
            e.setDamage(e.getDamage() * 1.5);
        }
    }


}
