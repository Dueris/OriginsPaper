package me.dueris.originspaper.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AddToSetEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Entity entity;
	private final String tag;

	public AddToSetEvent(Entity entity, String tag) {
		this.entity = entity;
		this.tag = tag;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public Entity getEntity() {
		return this.entity;
	}

	public String getTag() {
		return this.tag;
	}
}
