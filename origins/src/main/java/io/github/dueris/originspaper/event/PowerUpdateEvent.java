package io.github.dueris.originspaper.event;

import io.github.dueris.originspaper.power.factory.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PowerUpdateEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private final PowerType power;
	private final boolean removed;
	private final boolean isNew;

	public PowerUpdateEvent(@NotNull Player who, PowerType power, boolean removed, boolean isNew) {
		super(who);
		this.power = power;
		this.removed = removed;
		this.isNew = isNew;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public PowerType getPower() {
		return this.power;
	}

	public boolean isRemoved() {
		return this.removed;
	}

	public boolean isNew() {
		return this.isNew;
	}
}
