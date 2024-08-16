package io.github.dueris.originspaper.event;

import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class OriginChangeEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Origin origin;
	private final OriginLayer layer;
	private final boolean fromOrb;

	public OriginChangeEvent(@NotNull Player who, Origin origin, OriginLayer layer, boolean fromOrb) {
		super(who);
		this.origin = origin;
		this.layer = layer;
		this.fromOrb = fromOrb;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@NotNull
	public HandlerList getHandlers() {
		return handlers;
	}

	public Origin getOrigin() {
		return this.origin;
	}

	public boolean isFromOrb() {
		return this.fromOrb;
	}

	public OriginLayer getLayer() {
		return layer;
	}
}
