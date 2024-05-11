package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorldBorder;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class Overlay extends CraftPower implements Listener {
	private static final CraftWorldBorder border = (CraftWorldBorder) Bukkit.createWorldBorder();

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
	public void run(Player player, Power power) {
		if (Bukkit.getCurrentTick() % 2 == 0) return;
		if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) player)) {
			setActive(player, power.getTag(), true);
			initializeOverlay(player);
		} else {
			setActive(player, power.getTag(), false);
			deactivateOverlay(player);
		}
	}

	@Override
	public void doesntHavePower(Player p) {
		deactivateOverlay(p);
	}

	@Override
	public String getType() {
		return "apoli:overlay";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return overlay;
	}
}
