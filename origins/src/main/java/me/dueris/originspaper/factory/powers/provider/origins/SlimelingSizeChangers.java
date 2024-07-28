package me.dueris.originspaper.factory.powers.provider.origins;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.PowerUpdateEvent;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.factory.powers.provider.PowerProvider;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import me.dueris.originspaper.util.entity.PowerUtils;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class SlimelingSizeChangers implements Listener {

	@EventHandler
	public void onRejoin(PlayerJoinEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Player p = e.getPlayer();
				if (!PowerHolderComponent.hasPower(p, "origins:slime_skin")) return;
				double curSize = p.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
				double healthScale = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

				if (curSize >= 1.33) healthScale = 26;
				else if (curSize >= 1.2) healthScale = 22;
				else if (curSize >= 1.0) healthScale = 20;
				else if (curSize >= 0.8) healthScale = 16;
				else if (curSize >= 0.7) healthScale = 14;
				else healthScale = 14;

				p.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(curSize);
				p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(healthScale);
			}
		}.runTaskLater(OriginsPaper.getPlugin(), 20);
	}

	@EventHandler
	public void respawn(@NotNull PlayerPostRespawnEvent e) {
		Player p = e.getPlayer();
		if (!PowerHolderComponent.hasPower(p, "origins:slime_skin")) return;
		p.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(1);
		p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
	}

	public static class AddSize implements Listener, PowerProvider {
		protected static ResourceLocation powerReference = OriginsPaper.originIdentifier("slimeling_addsize");

		@EventHandler(priority = EventPriority.HIGHEST)
		public void powerGrant(@NotNull PowerUpdateEvent e) {
			if (!e.isRemoved() && (e.getPower().getTag().equalsIgnoreCase(powerReference.toString()) || e.getPower().getTag().equalsIgnoreCase("origins:slime_skin"))) {
				Player p = e.getPlayer();
				double curSize = p.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
				double healthScale = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
				if (e.getPower().getTag().equalsIgnoreCase(powerReference.toString())) {
					if (curSize < 1.33) {
						curSize = Math.min(1.33, curSize + 0.1);
					}

					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								PowerUtils.removePower(Bukkit.getConsoleSender(), e.getPower(), p, CraftApoli.getLayerFromTag("origins:origin"), false);
							} catch (InstantiationException | IllegalAccessException ex) {
								throw new RuntimeException(ex);
							}
						}
					}.runTaskLater(OriginsPaper.getPlugin(), 1);
				}

				if (curSize >= 1.33) healthScale = 26;
				else if (curSize >= 1.2) healthScale = 22;
				else if (curSize >= 1.0) healthScale = 20;
				else if (curSize >= 0.8) healthScale = 16;
				else if (curSize >= 0.7) healthScale = 14;
				else healthScale = 14;

				p.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(curSize);
				p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(healthScale);
			}
		}
	}

	public static class RemoveSize implements Listener, PowerProvider {
		protected static ResourceLocation powerReference = OriginsPaper.originIdentifier("slimeling_removesize");

		@EventHandler(priority = EventPriority.HIGHEST)
		public void powerGrant(@NotNull PowerUpdateEvent e) {
			if (!e.isRemoved() && (e.getPower().getTag().equalsIgnoreCase(powerReference.toString()) || e.getPower().getTag().equalsIgnoreCase("origins:slime_skin"))) {
				Player p = e.getPlayer();
				double curSize = p.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
				double healthScale = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
				if (e.getPower().getTag().equalsIgnoreCase(powerReference.toString())) {
					if (curSize > 0.66) {
						curSize = Math.max(0.66, curSize - 0.1);
					}

					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								PowerUtils.removePower(Bukkit.getConsoleSender(), e.getPower(), p, CraftApoli.getLayerFromTag("origins:origin"), false);
							} catch (InstantiationException | IllegalAccessException ex) {
								throw new RuntimeException(ex);
							}
						}
					}.runTaskLater(OriginsPaper.getPlugin(), 1);
				}

				if (curSize >= 1.33) healthScale = 26;
				else if (curSize >= 1.2) healthScale = 22;
				else if (curSize >= 1.0) healthScale = 20;
				else if (curSize >= 0.8) healthScale = 16;
				else if (curSize >= 0.7) healthScale = 14;
				else healthScale = 14;

				p.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(curSize);
				p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(healthScale);
			}
		}
	}
}
