package me.purplewolfmc.genesismc.core.origins.enderian;

import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class BrethrenOfEnd implements Listener {

    @EventHandler
    public void onLook(EntityTargetEvent e)
    {
        //EntityType en = e.getEntityType(); not needed
        if(e.getEntity() instanceof Enderman && (e.getTarget() instanceof Player)){
            Player p = (Player) e.getTarget();
            if(p.getScoreboardTags().contains("enderian")) {
                e.setCancelled(true);
            }
        }
    }
}

