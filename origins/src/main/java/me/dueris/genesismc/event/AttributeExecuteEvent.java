package me.dueris.genesismc.event;

import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class AttributeExecuteEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    Attribute attribute;
    Power power;

    public AttributeExecuteEvent(@NotNull Player who, Attribute attribute, Power power, boolean async) {
	super(who, async);
	this.attribute = attribute;
	this.power = power;
    }

    public static HandlerList getHandlerList() {
	return handlers;
    }

    @Override
    public HandlerList getHandlers() {
	return handlers;
    }

    public Attribute getAttribute() {
	return attribute;
    }

    public Power getPower() {
	return power;
    }

    @Override
    public String toString() {
	return "AttributeExecuteEvent{" +
	    "attribute=" + attribute +
	    ", power=" + power +
	    ", player=" + player +
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
}
