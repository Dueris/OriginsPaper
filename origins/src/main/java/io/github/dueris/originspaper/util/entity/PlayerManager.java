package io.github.dueris.originspaper.util.entity;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.event.PowerUpdateEvent;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.screen.GuiTicker;
import io.github.dueris.originspaper.storage.OriginConfiguration;
import io.github.dueris.originspaper.storage.PlayerPowerRepository;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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

import java.util.ArrayList;

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
		PlayerPowerRepository.getOrCreateRepo(((CraftPlayer) e.getPlayer()).getHandle()).readPowers(
				p.getPersistentDataContainer().has(identifier("powers")) ? p.getPersistentDataContainer().get(identifier("powers"), PersistentDataType.STRING) : "{}"
		);
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

		if (!p.getPersistentDataContainer().has(identifier("in-phantomform"), PersistentDataType.BOOLEAN)) {
			p.getPersistentDataContainer().set(identifier("in-phantomform"), PersistentDataType.BOOLEAN, false);
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
		} catch (Exception ignored) {
		}

		p.saveData();
		PowerHolderComponent.loadPowers(p);
		GuiTicker.delayedPlayers.add(p);
		(new BukkitRunnable() {
			public void run() {
				GuiTicker.delayedPlayers.remove(p);
			}
		}).runTaskLater(OriginsPaper.getPlugin(), OriginConfiguration.getConfiguration().getInt("choosing_delay"));
		OriginLayer originLayer = OriginsPaper.getLayer(ResourceLocation.parse("origins:origin"));
		if (!p.getPersistentDataContainer().has(identifier("updated"))
				&& !PowerHolderComponent.getOrigin(p, originLayer).equals(OriginsPaper.EMPTY_ORIGIN)) {
			PowerHolderComponent.setOrigin(
					p, originLayer, PowerHolderComponent.getOrigin(p, originLayer)
			);
			p.getPersistentDataContainer().set(identifier("updated"), PersistentDataType.BOOLEAN, true);
		}
	}

	@EventHandler
	public void playerQuitHandler(PlayerQuitEvent e) {
		playersLeaving.add(e.getPlayer());
		e.getPlayer().saveData();
		String saveData = PlayerPowerRepository.getOrCreateRepo(((CraftPlayer) e.getPlayer()).getHandle()).serializePowers(new CompoundTag()).toString();
		e.getPlayer().getPersistentDataContainer()
				.set(identifier("powers"), PersistentDataType.STRING, saveData);
		PowerHolderComponent.unloadPowers(e.getPlayer());
		e.getPlayer().saveData();
		playersLeaving.remove(e.getPlayer());
	}

	@EventHandler
	public void powerUpdate(PowerUpdateEvent e) {
		Player p = e.getPlayer();
		p.setGravity(true);
	}
}
