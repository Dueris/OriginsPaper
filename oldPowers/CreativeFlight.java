package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class CreativeFlight extends CraftPower implements Listener {

	@Override
	public void runAsync(Player p, Power power) {
		GameMode m = p.getGameMode();
		NamespacedKey insideBlock = new NamespacedKey(GenesisMC.getPlugin(), "insideBlock");
		PersistentDataContainer container = p.getPersistentDataContainer();
		if (container.get(insideBlock, PersistentDataType.BOOLEAN) != null && Boolean.TRUE.equals(container.get(insideBlock, PersistentDataType.BOOLEAN))) {
			if (p.getAllowFlight()) {
				p.setFlying(true);
			}
		} else {
			if (creative_flight.contains(p) || OriginPlayerAccessor.isInPhantomForm(p)) {
				if (!p.getAllowFlight()) p.setAllowFlight(true);
				if (p.isFlying()) {
					p.setFlying(true);
				}
			} else {
				boolean a = m.equals(GameMode.SPECTATOR) || m.equals(GameMode.CREATIVE) || ElytraFlightPower.elytra.contains(p) || no_gravity.contains(p) || grounded.contains(p) || swimming.contains(p);
				if (a && !p.getAllowFlight()) {
					p.setAllowFlight(true);
				} else if (!a && p.getAllowFlight()) {
					p.setAllowFlight(false);
				}

				if (ElytraFlightPower.elytra.contains(p)) {
					p.setFlying(m.equals(GameMode.CREATIVE) || m.equals(GameMode.SPECTATOR));
				}
			}
		}
		if (p.getChunk().isLoaded()) {
			if (Phasing.inPhantomFormBlocks.contains(p)) { // Intended only for phantom form
				container.set(insideBlock, PersistentDataType.BOOLEAN, true);
			} else {
				container.set(insideBlock, PersistentDataType.BOOLEAN, false);
			}
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		run(e.getPlayer(), null);
	}

	@EventHandler
	public void choosse(OriginChangeEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				CreativeFlight fl = new CreativeFlight();
				fl.run(e.getPlayer(), null);
			}

		}.runTaskLater(GenesisMC.getPlugin(), 10L);
	}

	@Override
	public String getType() {
		return "apoli:creative_flight";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return creative_flight;
	}

}
