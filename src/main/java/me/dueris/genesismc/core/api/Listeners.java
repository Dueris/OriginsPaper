package me.dueris.genesismc.core.api;

import me.dueris.genesismc.core.api.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Listeners implements Listener {
    @EventHandler
    public void orbevent(OrbInteractEvent e) {

    }

    @EventHandler
    public void originchooseevent(OriginChooseEvent e) {

    }

    @EventHandler
    public void origincommandevent(OriginCommandEvent e) {
    }

    @EventHandler
    public void originkeybindevent(OriginKeybindExecuteEvent e) {

    }

    @EventHandler
    public void originpacketevent(OriginPacketSendEvent e) {

    }

    @EventHandler
    public void waterprotgenevent(WaterProtectionGenerateEvent e) {
    }

    @EventHandler
    public void executeChooseEvent(OriginChooseEvent e) {
        e.getPlayer().sendMessage("choose");
    }
}
