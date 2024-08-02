package me.dueris.originspaper.util.entity;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.OriginChangeEvent;
import me.dueris.originspaper.event.PowerUpdateEvent;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.registry.registries.Origin;
import me.dueris.originspaper.registry.registries.OriginLayer;
import me.dueris.originspaper.screen.GuiTicker;
import me.dueris.originspaper.storage.OriginConfiguration;
import me.dueris.originspaper.storage.OriginDataContainer;
import me.dueris.originspaper.storage.nbt.NBTFixerUpper;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayerManager implements Listener {
	public static ArrayList<Player> firstJoin = new ArrayList<>();
	public static ArrayList<Player> playersLeaving = new ArrayList<>();

	private static NamespacedKey identifier(String id) {
		return new NamespacedKey(OriginsPaper.getPlugin(), id);
	}

	@EventHandler(
		priority = EventPriority.HIGHEST
	)
	public void playerJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		PowerHolderComponent.powersAppliedList.putIfAbsent(p, new ConcurrentLinkedQueue<>());

		for (NamespacedKey key : p.getPersistentDataContainer().getKeys()) {
			if (key.asString().equalsIgnoreCase("genesismc:originlayer")) {
				p.getPersistentDataContainer()
					.set(identifier("originLayer"), PersistentDataType.STRING, p.getPersistentDataContainer().get(key, PersistentDataType.STRING));
				p.getPersistentDataContainer().remove(key);
			}
		}

		if (!p.getPersistentDataContainer().has(identifier("originLayer"), PersistentDataType.STRING)
			|| p.getPersistentDataContainer().get(identifier("originLayer"), PersistentDataType.STRING) == null
			|| p.getPersistentDataContainer().get(identifier("originLayer"), PersistentDataType.STRING).equalsIgnoreCase("")) {
			HashMap<OriginLayer, Origin> origins = new HashMap<>();

			for (OriginLayer layer : CraftApoli.getLayersFromRegistry()) {
				origins.put(layer, CraftApoli.emptyOrigin());
			}

			p.getPersistentDataContainer().set(identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(origins));
			firstJoin.add(p);
		}

		if (!p.getPersistentDataContainer().has(identifier("insideblock"), PersistentDataType.BOOLEAN)) {
			p.getPersistentDataContainer().set(identifier("insideblock"), PersistentDataType.BOOLEAN, false);
		}

		PersistentDataContainer data = p.getPersistentDataContainer();
		if (data.has(identifier("shulker-box"), PersistentDataType.STRING)) {
			String save = data.get(identifier("shulker-box"), PersistentDataType.STRING);
			PersistentDataContainer container = p.getPersistentDataContainer();
			container.set(
				CraftNamespacedKey.fromMinecraft(
					OriginsPaper.apoliIdentifier("inventorydata_" + "origins:inventory".replace(":", "_").replace("/", "_").replace("\\", "_"))
				),
				PersistentDataType.STRING,
				save
			);
			p.saveData();
			data.remove(identifier("shulker-box"));
		}

		if (!p.getPersistentDataContainer().has(identifier("can-explode"), PersistentDataType.INTEGER)) {
			p.getPersistentDataContainer().set(identifier("can-explode"), PersistentDataType.INTEGER, 1);
		}

		if (!p.getPersistentDataContainer().has(identifier("in-phantomform"), PersistentDataType.BOOLEAN)) {
			p.getPersistentDataContainer().set(identifier("in-phantomform"), PersistentDataType.BOOLEAN, false);
		}

		if (!p.getPersistentDataContainer().has(identifier("toggle"), PersistentDataType.INTEGER)) {
			p.getPersistentDataContainer().set(identifier("toggle"), PersistentDataType.INTEGER, 1);
		}

		try {
			if (!p.getPersistentDataContainer().has(identifier("modified-skin-url"), PersistentDataType.STRING)
				|| p.getPersistentDataContainer().get(identifier("modified-skin-url"), PersistentDataType.STRING) == null) {
				p.getPersistentDataContainer()
					.set(identifier("modified-skin-url"), PersistentDataType.STRING, p.getPlayerProfile().getTextures().getSkin().getFile());
			}

			if (!p.getPersistentDataContainer().has(identifier("original-skin-url"), PersistentDataType.STRING)
				|| p.getPersistentDataContainer().get(identifier("original-skin-url"), PersistentDataType.STRING) == null) {
				p.getPersistentDataContainer()
					.set(identifier("original-skin-url"), PersistentDataType.STRING, p.getPlayerProfile().getTextures().getSkin().getFile());
			}
		} catch (Exception var7) {
		}

		p.saveData();

		try {
			NBTFixerUpper.fixupFile(
				Path.of(OriginsPaper.playerDataFolder.toPath() + File.separator + ((CraftPlayer) p).getHandle().getStringUUID() + ".dat").toFile()
			);
		} catch (IOException var6) {
			var6.printStackTrace();
		}

		OriginDataContainer.loadData(p);
		PowerHolderComponent.setupPowers(p);
		PowerHolderComponent.assignPowers(p);
		GuiTicker.delayedPlayers.add(p);
		(new BukkitRunnable() {
			public void run() {
				GuiTicker.delayedPlayers.remove(p);
			}
		}).runTaskLater(OriginsPaper.getPlugin(), OriginConfiguration.getConfiguration().getInt("choosing_delay"));
		if (!p.getPersistentDataContainer().has(identifier("updated"))
			&& !PowerHolderComponent.getOrigin(p, CraftApoli.getLayer("origins:origin")).equals(CraftApoli.emptyOrigin())) {
			PowerHolderComponent.setOrigin(
				p, CraftApoli.getLayer("origins:origin"), PowerHolderComponent.getOrigin(p, CraftApoli.getLayer("origins:origin"))
			);
			p.getPersistentDataContainer().set(identifier("updated"), PersistentDataType.BOOLEAN, true);
		}
	}

	@EventHandler
	public void playerQuitHandler(PlayerQuitEvent e) {
		playersLeaving.add(e.getPlayer());
		String serializedData = CraftApoli.toSaveFormat(PowerHolderComponent.getOrigin(e.getPlayer()), e.getPlayer());
		e.getPlayer().getPersistentDataContainer().set(identifier("originLayer"), PersistentDataType.STRING, serializedData);
		e.getPlayer().saveData();
		PowerHolderComponent.unassignPowers(e.getPlayer());
		OriginDataContainer.unloadData(e.getPlayer());
		playersLeaving.remove(e.getPlayer());
	}

	@EventHandler
	public void newOrigin(OriginChangeEvent e) {
		OriginDataContainer.unloadData(e.getPlayer());
		OriginDataContainer.loadData(e.getPlayer());
	}

	@EventHandler
	public void powerUpdate(PowerUpdateEvent e) {
		Player p = e.getPlayer();
		p.setGravity(true);
	}
}
