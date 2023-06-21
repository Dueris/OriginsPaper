package me.dueris.genesismc.core.origins;

import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OriginSavePatch implements Listener {
    @EventHandler
    public void Patch(PlayerJoinEvent e){
        Player p = e.getPlayer();

    }
}
