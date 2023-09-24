package me.dueris.genesismc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WaterProtectionGenerateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
