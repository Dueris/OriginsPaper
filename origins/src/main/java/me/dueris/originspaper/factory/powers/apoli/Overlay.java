package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.PowerUpdateEvent;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class Overlay extends PowerType {
	private static final CraftWorldBorder border = (CraftWorldBorder) Bukkit.createWorldBorder();

	public Overlay(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("overlay"));
	}

	public static void initializeOverlay(@NotNull Player player) {
		border.setCenter(player.getWorld().getWorldBorder().getCenter());
		border.setSize(player.getWorld().getWorldBorder().getSize());
		border.setWarningDistance(999999999);
		player.setWorldBorder(border);
	}

	public static void deactivateOverlay(@NotNull Player player) {
		player.setWorldBorder(player.getWorld().getWorldBorder());
	}

	@EventHandler
	public void remove(@NotNull PowerUpdateEvent e) {
		if (e.isRemoved() && e.getPower().getType().equals(getType())) {
			deactivateOverlay(e.getPlayer());
		}
	}

	@Override
	public void tick(Player player) {
		if (Bukkit.getCurrentTick() % 2 == 0) return;
		if (isActive(player)) {
			initializeOverlay(player);
		} else {
			deactivateOverlay(player);
		}
	}
}
