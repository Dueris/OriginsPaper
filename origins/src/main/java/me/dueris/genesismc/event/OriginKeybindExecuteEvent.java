package me.dueris.genesismc.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OriginKeybindExecuteEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	static String key_type;
	static ItemStack item;

	public OriginKeybindExecuteEvent(@NotNull Player who, String key_type, ItemStack itemTrigger) {
		super(who);
		OriginKeybindExecuteEvent.key_type = key_type;
		item = itemTrigger;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public String getKey() {
		return key_type;
	}

	public ItemStack getItemTrigger() {
		return item;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
