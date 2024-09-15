package io.github.dueris.originspaper.util;

import io.github.dueris.calio.util.holder.ObjectProvider;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.event.PowerUpdateEvent;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.storage.OriginComponent;
import io.github.dueris.originspaper.storage.OriginConfiguration;
import io.github.dueris.originspaper.storage.PlayerPowerRepository;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class PlayerManager implements Listener {
	public static LinkedList<Player> firstJoin = new LinkedList<>();
	public static LinkedList<Player> playersLeaving = new LinkedList<>();

	private static @NotNull NamespacedKey identifier(String id) {
		return new NamespacedKey(OriginsPaper.getPlugin(), id);
	}

	@EventHandler(
		priority = EventPriority.LOWEST
	)
	public void playerJoin(@NotNull PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		PlayerPowerRepository.getOrCreateRepo(((CraftPlayer) e.getPlayer()).getHandle()).readPowers(
			p.getPersistentDataContainer().has(identifier("apoli_repository")) ? p.getPersistentDataContainer().get(identifier("apoli_repository"), PersistentDataType.STRING) : ((ObjectProvider<String>) () -> {
				firstJoin.add(p);
				return "{}";
			}).get()
		);

		if (!p.getPersistentDataContainer().has(identifier("in-phantomform"), PersistentDataType.BOOLEAN)) {
			p.getPersistentDataContainer().set(identifier("in-phantomform"), PersistentDataType.BOOLEAN, false);
		}

		p.saveData();
		PowerHolderComponent.loadPowers(p);
		Scheduler.delayedPlayers.add(p);
		(new BukkitRunnable() {
			public void run() {
				Scheduler.delayedPlayers.remove(p);
			}
		}).runTaskLater(OriginsPaper.getPlugin(), OriginConfiguration.getConfiguration().getInt("choosing_delay"));
		OriginLayer originLayer = OriginsPaper.getLayer(ResourceLocation.parse("origins:origin"));
		if (!p.getPersistentDataContainer().has(identifier("updated"))
			&& !OriginComponent.getOrigin(p, originLayer).equals(Origin.EMPTY)) {
			OriginComponent.setOrigin(
				p, originLayer, OriginComponent.getOrigin(p, originLayer)
			);
			p.getPersistentDataContainer().set(identifier("updated"), PersistentDataType.BOOLEAN, true);
		}
	}

	@EventHandler
	public void playerQuitHandler(@NotNull PlayerQuitEvent e) {
		playersLeaving.add(e.getPlayer());
		e.getPlayer().saveData();
		ServerPlayer nms = ((CraftPlayer) e.getPlayer()).getHandle();
		String saveData = PlayerPowerRepository.getOrCreateRepo(nms).serializePowers(new CompoundTag()).toString();
		e.getPlayer().getPersistentDataContainer()
			.set(identifier("apoli_repository"), PersistentDataType.STRING, saveData);
		PowerHolderComponent.unloadPowers(e.getPlayer());
		e.getPlayer().saveData();
		playersLeaving.remove(e.getPlayer());
	}

	@EventHandler
	public void powerUpdate(@NotNull PowerUpdateEvent e) {
		Player p = e.getPlayer();
		p.setGravity(true);
	}
}
