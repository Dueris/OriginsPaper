package me.dueris.genesismc.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RemoveFromSetEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Entity entity;
	private final String tag;

	public RemoveFromSetEvent(Entity entity, String tag) {
		this.entity = entity;
		this.tag = tag;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
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
