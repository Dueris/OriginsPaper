package me.dueris.genesismc.event;

import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PowerUpdateEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private static Power power;
	private static boolean removed;

	public PowerUpdateEvent(@NotNull Player who, Power power, boolean removed) {
		super(who);
		PowerUpdateEvent.power = power;
		PowerUpdateEvent.removed = removed;
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
}
