package me.dueris.originspaper.event;

import me.dueris.originspaper.power.PowerType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class AttributeExecuteEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	Attribute attribute;
	PowerType power;

	public AttributeExecuteEvent(@NotNull Player who, Attribute attribute, PowerType power, boolean async) {
		super(who, async);
		this.attribute = attribute;
		this.power = power;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public Attribute getAttribute() {
		return this.attribute;
	}

	public PowerType getPower() {
		return this.power;
	}

	public String toString() {
		return "AttributeExecuteEvent{attribute=" + this.attribute + ", power=" + this.power + ", player=" + this.player + "}";
	}

	public boolean equals(Object obj) {
		return super.equals(obj);
	}


}
