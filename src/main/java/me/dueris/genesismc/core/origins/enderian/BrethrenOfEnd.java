package me.dueris.genesismc.core.origins.enderian;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BrethrenOfEnd implements Listener {

    @EventHandler
    public void onLook(EntityTargetEvent e) {
        //EntityType en = e.getEntityType(); not needed
        if (e.getEntity() instanceof Enderman && (e.getTarget() instanceof Player)) {

            Player p = (Player) e.getTarget();
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 0401065) {
                e.setCancelled(true);
            }
        }
    }
}

