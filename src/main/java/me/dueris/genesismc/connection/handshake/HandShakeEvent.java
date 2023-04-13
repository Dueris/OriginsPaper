package me.dueris.genesismc.connection.handshake;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class HandShakeEvent implements Listener {
//handshake for player connection
    //send packet, if get return, set true and mark player as immersion-user.
    //send packtet, if not return, set false and mark player as not immersion-user.
    @EventHandler
    public void onJoin(PlayerJoinEvent e){

    }

}
