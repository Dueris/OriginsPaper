package me.dueris.genesismc.factory.powers.OriginsMod.genesismc;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.dueris.genesismc.factory.powers.Power.bow_nope;

public class BowInability implements Listener {

    @EventHandler
    public void onUseBow(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (bow_nope.contains(e.getPlayer())) {
            if (e.getItem() != null) {
                if (e.getItem().getType().equals(Material.BOW)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}

