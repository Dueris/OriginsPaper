package me.dueris.genesismc.event;

import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PowerUpdateEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Power power;
	private final boolean removed;
	private final boolean isNew;

	public PowerUpdateEvent(@NotNull Player who, Power power, boolean removed, boolean isNew) {
		super(who, true);
		this.power = power;
		this.removed = removed;
		this.isNew = isNew;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Power getPower() {
		return power;
	}

	public boolean isRemoved() {
		return removed;
	}

	public boolean isNew() {
		return isNew;
	}
}
