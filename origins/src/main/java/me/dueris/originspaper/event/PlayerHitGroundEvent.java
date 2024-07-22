package me.dueris.originspaper.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerHitGroundEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	public PlayerHitGroundEvent(@NotNull Player who) {
		super(who);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}
