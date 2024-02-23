package me.dueris.genesismc.event;

import me.dueris.genesismc.registry.registries.Origin;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class OriginChangeEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Origin origin;

    public OriginChangeEvent(@NotNull Player who, Origin origin) {
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

    public Origin getOrigin() {
        return origin;
    }
}