package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.genesismc.GravityPower;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import org.bukkit.Bukkit;
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

public class CreativeFlight extends PowerType {

	@Override
	public void tickAsync() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			GameMode m = p.getGameMode();
			NamespacedKey insideBlock = new NamespacedKey(GenesisMC.getPlugin(), "insideBlock");
			PersistentDataContainer container = p.getPersistentDataContainer();
			if (container.get(insideBlock, PersistentDataType.BOOLEAN) != null && Boolean.TRUE.equals(container.get(insideBlock, PersistentDataType.BOOLEAN))) {
				if (p.getAllowFlight()) {
					p.setFlying(true);
				}
			} else {
				if (getPlayers().contains(p) || PowerHolderComponent.isInPhantomForm(p)) {
					if (!p.getAllowFlight()) p.setAllowFlight(true);
					if (p.isFlying()) {
						p.setFlying(true);
					}
				} else {
					boolean a = m.equals(GameMode.SPECTATOR) || m.equals(GameMode.CREATIVE) ||
						PowerHolderComponent.hasPowerType(p, ElytraFlight.class) || PowerHolderComponent.hasPowerType(p, GravityPower.class) ||
						PowerHolderComponent.hasPowerType(p, Grounded.class) || PowerHolderComponent.hasPowerType(p, Swimming.class);
					if (a && !p.getAllowFlight()) {
						p.setAllowFlight(true);
					} else if (!a && p.getAllowFlight()) {
						p.setAllowFlight(false);
					}

					if (PowerHolderComponent.hasPowerType(p, ElytraFlight.class)) {
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
	}

}
