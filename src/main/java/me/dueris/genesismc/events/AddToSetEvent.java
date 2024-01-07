package me.dueris.genesismc.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AddToSetEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Entity entity;
    private String tag;
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public AddToSetEvent(Entity entity, String tag){
        this.entity = entity;
        this.tag = tag;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public String getTag() {
        return this.tag;
    }
}