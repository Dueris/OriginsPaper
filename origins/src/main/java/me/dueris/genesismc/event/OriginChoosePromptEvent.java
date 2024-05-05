package me.dueris.genesismc.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class OriginChoosePromptEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private boolean canceled = false;

	public OriginChoosePromptEvent(@NotNull Player who) {
		super(who, true);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public boolean isCanceled() {
		return this.canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
}
