package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Overlay extends PowerType implements Listener {
	private static final CraftWorldBorder border = (CraftWorldBorder) Bukkit.createWorldBorder();

	@Register
	public Overlay(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("overlay"));
	}

	public static void initializeOverlay(Player player) {
		border.setCenter(player.getWorld().getWorldBorder().getCenter());
		border.setSize(player.getWorld().getWorldBorder().getSize());
		border.setWarningDistance(999999999);
		player.setWorldBorder(border);
	}

	public static void deactivateOverlay(Player player) {
		player.setWorldBorder(player.getWorld().getWorldBorder());
	}

	@EventHandler
	public void remove(PowerUpdateEvent e) {
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
