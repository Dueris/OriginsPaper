package me.dueris.genesismc.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;

public class GiveOrbCommandExtentsion implements Listener {

    @EventHandler
    public void ProccessGive(PlayerCommandPreprocessEvent e){
        Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
        List<String> playernames = new ArrayList<>();
        Bukkit.getServer().getOnlinePlayers().toArray(players);
        if(e.getMessage().equalsIgnoreCase("/give @s genesis:orb_of_origin")){
            e.setCancelled(true);
            e.getPlayer().getInventory().addItem(orb);
        }
    }

}
