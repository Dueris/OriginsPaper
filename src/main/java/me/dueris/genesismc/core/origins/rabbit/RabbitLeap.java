package me.dueris.genesismc.core.origins.rabbit;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class RabbitLeap implements Listener {

    @EventHandler
    public void onRabbitLeap(PlayerToggleSneakEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 5308033) {
            ;
        }
    }
}
