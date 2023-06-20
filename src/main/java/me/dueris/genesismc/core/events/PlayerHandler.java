package me.dueris.genesismc.core.events;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerHandler implements Listener {

    @EventHandler
    public void playerJoinListener(PlayerJoinEvent e) {
        OriginPlayer.assignPowers(e.getPlayer());

        //resets the origin on join
//        if (!origin.getTag().equals(CraftApoli.nullOrigin().getTag())) {
//            OriginPlayer.setOrigin(player, origin);
//        }

    }

    @EventHandler
    public void playerQuitListener(PlayerQuitEvent e) {
        OriginPlayer.unassignPowers(e.getPlayer());
    }
}
