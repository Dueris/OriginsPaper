package me.dueris.genesismc.core.factory.handlers;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerHandler implements Listener {

    @EventHandler
    public void playerJoinListener(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        //OriginPlayer.assignPowers(player);
    }

    @EventHandler
    public void playerQuitListener(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        //OriginPlayer.unassignPowers(player);
    }
}
