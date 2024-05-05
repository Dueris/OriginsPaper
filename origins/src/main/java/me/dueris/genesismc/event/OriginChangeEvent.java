package me.dueris.genesismc.event;

import me.dueris.genesismc.registry.registries.Origin;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class OriginChangeEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Origin origin;
	private final boolean fromOrb;

	public OriginChangeEvent(@NotNull Player who, Origin origin, boolean fromOrb) {
		super(who);
		this.origin = origin;
		this.fromOrb = fromOrb;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public Origin getOrigin() {
		return origin;
	}

	public boolean isFromOrb() {
		return fromOrb;
	}
}