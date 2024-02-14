package me.dueris.genesismc.event;

import me.dueris.genesismc.registry.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class OriginChangeEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final OriginContainer origin;

    public OriginChangeEvent(@NotNull Player who, OriginContainer origin) {
        super(who);
        this.origin = origin;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public OriginContainer getOrigin() {
        return origin;
    }
}