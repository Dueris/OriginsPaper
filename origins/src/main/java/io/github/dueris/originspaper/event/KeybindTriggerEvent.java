package io.github.dueris.originspaper.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class KeybindTriggerEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private final String key_type;

	public KeybindTriggerEvent(@NotNull Player who, String key_type) {
		super(who);
		this.key_type = key_type;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public String getKey() {
		return key_type;
	}

	public @NotNull HandlerList getHandlers() {
		return handlers;
	}
}
