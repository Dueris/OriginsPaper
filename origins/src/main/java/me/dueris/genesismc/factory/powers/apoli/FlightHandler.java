package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class FlightHandler extends CraftPower {

	@Override
	public void run(Player p) {
		if (p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN) != null && Boolean.TRUE.equals(p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN))) {
			if (p.getAllowFlight()) {
				p.setFlying(true);
			}
		} else {
			if (p.getGameMode().equals(GameMode.CREATIVE)) {
				p.setAllowFlight(true);
			} else {
				if (creative_flight.contains(p) || OriginPlayerAccessor.isInPhantomForm(p)) {
					p.setAllowFlight(true);
					if (p.isFlying()) {
						p.setFlying(true);
					}
				} else {
					p.setAllowFlight(p.getGameMode().equals(GameMode.SPECTATOR) || FlightElytra.elytra.contains(p) || no_gravity.contains(p));
					if (FlightElytra.elytra.contains(p)) {
						p.setFlying(p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR));
					}
				}
			}
		}
		if (p.getChunk().isLoaded()) {
			if (Phasing.inPhantomFormBlocks.contains(p)) { // Intended only for phantom form
				p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, true);
			} else {
				p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, false);
			}
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		run(e.getPlayer());
	}

	@EventHandler
	public void choosse(OriginChangeEvent e) {
		new BukkitRunnable() {

			@Override
			public void run() {
				FlightHandler fl = new FlightHandler();
				fl.run(e.getPlayer());
			}

		}.runTaskLater(GenesisMC.getPlugin(), 10L);
	}

	@Override
	public String getPowerFile() {
		return "apoli:creative_flight";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return creative_flight;
	}

}
