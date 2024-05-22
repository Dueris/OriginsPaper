package me.dueris.genesismc.event;

import me.dueris.genesismc.factory.powers.holder.PowerType;
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
		super(who, false);
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

	public PowerType getPower() {
		return power;
	}

	public boolean isRemoved() {
		return removed;
	}

	public boolean isNew() {
		return isNew;
	}
}
