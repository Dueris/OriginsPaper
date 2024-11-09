package io.github.dueris.originspaper.screen;

import io.github.dueris.originspaper.component.OriginComponent;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.origin.OriginLayerManager;
import io.github.dueris.originspaper.registry.ModItems;
import io.github.dueris.originspaper.util.Scheduler;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class ScreenListener implements Listener {

	public ScreenListener() {
		Scheduler.INSTANCE.repeating((m) -> {
			for (ServerPlayer player : ChooseOriginScreen.CURRENTLY_RENDERING) {
				CraftPlayer craftPlayer = player.getBukkitEntity();
				InventoryView view = ChooseOriginScreen.RENDER_MAP.get(player).inventoryView;
				if (view == null || craftPlayer.getOpenInventory() == view) {
					continue;
				}
				craftPlayer.openInventory(view);
			}
			for (ServerPlayer player : m.getPlayerList().players) {
				if (!ChooseOriginScreen.CURRENTLY_RENDERING.contains(player)) {
					OriginComponent component = OriginComponent.ORIGIN.get(player);
					for (OriginLayer layer : OriginLayerManager.values()) {
						if (!component.hasOrigin(layer)) {
							new ChooseOriginScreen(player);
						}
					}
				}
			}
		}, 1, 1);
	}

	@EventHandler
	public void onInteract(@NotNull InventoryClickEvent e) {
		ServerPlayer player = ((CraftPlayer) e.getWhoClicked()).getHandle();
		if (ChooseOriginScreen.CURRENTLY_RENDERING.contains(player) && e.getCurrentItem() != null) {
			e.setCancelled(true);
			ChooseOriginScreen screen = ChooseOriginScreen.RENDER_MAP.get(player);
			if (e.getCurrentItem().isSimilar(ChooseOriginScreen.NEXT_STACK.asBukkitMirror())) {
				screen.next();
				screen.update();
			} else if (e.getCurrentItem().isSimilar(ChooseOriginScreen.BACK_STACK.asBukkitMirror())) {
				screen.back();
				screen.update();
			} else if (e.getCurrentItem().isSimilar(screen.getCurrentOrigin() == null ? ModItems.ORB_OF_ORIGINS.asBukkitMirror() : screen.getCurrentOrigin().getDisplayItem().asBukkitMirror())) {
				screen.choose();
			}
		}
	}
}
