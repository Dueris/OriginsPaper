package me.dueris.genesismc.core.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class OriginPacketSendEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public OriginPacketSendEvent(@NotNull Player who) {
        super(who);
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }
}
