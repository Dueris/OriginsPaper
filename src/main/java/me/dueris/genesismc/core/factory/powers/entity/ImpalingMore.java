package me.dueris.genesismc.core.factory.powers.entity;

import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ImpalingMore implements Listener {

    @EventHandler
    public void ImpalingEvent(EntityDamageByEntityEvent e){
        if(!(e.getEntity() instanceof Player p)) return;
        if(e.getDamager() instanceof Trident){
            e.setDamage(e.getDamage() * 1.5);
        }
    }


}
