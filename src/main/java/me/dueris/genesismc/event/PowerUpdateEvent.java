package me.dueris.genesismc.event;

import me.dueris.genesismc.registry.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PowerUpdateEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private static PowerContainer power;
    private static boolean removed;

    public PowerUpdateEvent(@NotNull Player who, PowerContainer power, boolean removed) {
        super(who);
        this.power = power;
        this.removed = removed;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public PowerContainer getPower() {
        return power;
    }

    public boolean isRemoved() {
        return removed;
    }
}
