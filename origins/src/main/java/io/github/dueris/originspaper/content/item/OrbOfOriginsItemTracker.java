package io.github.dueris.originspaper.content.item;

import io.github.dueris.originspaper.component.OriginComponent;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.origin.OriginLayerManager;
import io.github.dueris.originspaper.registry.ModItems;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class OrbOfOriginsItemTracker implements Listener {

	@EventHandler
	public void onInteract(@NotNull PlayerInteractEvent e) {
		if (e.getItem() != null && e.getItem().equals(ModItems.ORB_OF_ORIGINS.asBukkitMirror())) {
			ServerPlayer player = ((CraftPlayer) e.getPlayer()).getHandle();
			OriginComponent component = OriginComponent.ORIGIN.get(player);
			for (OriginLayer layer : OriginLayerManager.values()) {
				component.setOrigin(layer, Origin.EMPTY);
				e.getItem().setAmount(0);
			}
		}
	}
}
