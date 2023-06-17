package me.dueris.genesismc.core.events;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerHandler implements Listener {

    @EventHandler
    public void playerJoinListener(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        //resets the origin on join
        OriginContainer origin = OriginPlayer.getOrigin(player);
        if (!origin.getTag().equals(CraftApoli.nullOrigin().getTag())) {
            OriginPlayer.setOrigin(player, origin);
        }

        OriginPlayer.assignPowers(player);
    }

    @EventHandler
    public void playerQuitListener(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        OriginPlayer.unassignPowers(player);
    }
}
