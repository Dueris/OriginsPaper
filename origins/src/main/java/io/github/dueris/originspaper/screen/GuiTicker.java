package io.github.dueris.originspaper.screen;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.event.OriginChoosePromptEvent;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static io.github.dueris.originspaper.screen.ScreenNavigator.inChoosingLayer;

public class GuiTicker extends BukkitRunnable {
	public static List<Player> delayedPlayers = new ArrayList<>();

	@Override
	public void run() {
		if (ScreenNavigator.layerPages.isEmpty()) return; // No pages to display.
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (delayedPlayers.contains(p)) continue;
			for (OriginLayer layer : OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER).values().stream().filter(OriginLayer::isEnabled).toList()) {
				if (layer.testChoosable(((CraftPlayer) p).getHandle()).isEmpty()) continue;
				try {
					if (PowerHolderComponent.getOrigin(p, layer).getTag().equalsIgnoreCase("origins:empty")) {
						if (layer.testDefaultOrigin(((CraftPlayer) p).getHandle())) continue;
						if (!inChoosingLayer.containsKey(((CraftPlayer) p).getHandle())) {
							OriginChoosePromptEvent event = new OriginChoosePromptEvent(p);
							Bukkit.getPluginManager().callEvent(event);
							if (!event.isCanceled()) {
								ScreenNavigator.open(p, layer, false);
							}
						}
					}
					p.setInvulnerable(inChoosingLayer.containsKey(((CraftPlayer) p).getHandle()));
				} catch (Exception e) {
					p.getPersistentDataContainer().remove(new NamespacedKey(OriginsPaper.getPlugin(), "originLayer"));
					e.printStackTrace();
				}
			}
		}
	}
}
