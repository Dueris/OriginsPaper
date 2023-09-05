package me.dueris.genesismc.events;

import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class ToggleTriggerEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    String key;
    Boolean enabled;
    OriginContainer origin;

    public ToggleTriggerEvent(@NotNull Player who, String key, OriginContainer origin, Boolean enabled) {
        super(who);
        this.enabled = enabled;
        this.key = key;
        this.origin = origin;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }

    public OriginContainer getOrigin() {
        return origin;
    }

    @Override
    public @NotNull String getEventName() {
        return super.getEventName();
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "ToggleTriggerEvent{" +
                "key='" + key + '\'' +
                ", origin=" + origin +
                ", enabled=" + enabled +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean callEvent() {
        return super.callEvent();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
