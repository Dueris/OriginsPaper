package io.github.dueris.originspaper.screen;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class ScreenListener implements Listener {

	@EventHandler
	public void onInteract(@NotNull PlayerInteractEvent e) {
		ServerPlayer player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (ChooseOriginScreen.CURRENTLY_RENDERING.contains(player)) {
			e.setCancelled(true);
		}
	}
}
