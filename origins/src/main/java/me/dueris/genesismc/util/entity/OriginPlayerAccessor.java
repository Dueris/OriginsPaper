package me.dueris.genesismc.util.entity;

import javassist.NotFoundException;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.factory.powers.genesismc.GravityPower;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.storage.OriginConfiguration;
import me.dueris.genesismc.storage.OriginDataContainer;
import me.dueris.genesismc.util.Metrics;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import static me.dueris.genesismc.screen.ScreenNavigator.inChoosingLayer;

public class OriginPlayerAccessor implements Listener {

	// Power maps of every power based on each layer applied to the player
	public static ConcurrentHashMap<Player, HashMap<Layer, ConcurrentLinkedQueue<Power>>> playerPowerMapping = new ConcurrentHashMap<>();
	// A list of CraftPowers to be ran on the player
	public static ConcurrentHashMap<Player, ConcurrentLinkedQueue<ApoliPower>> powersAppliedList = new ConcurrentHashMap<>();
	// A list of Players that have powers that should be run
	public static ConcurrentLinkedQueue<Player> hasPowers = new ConcurrentLinkedQueue<>();
	/**
	 * For some reason, a mod on the client breaks the ability to check the
	 * SharedConstant value retrieved and set in Player#isSprinting(), but it still sends
	 * the sprinting state update to the server. This is a workaround to ensure that
	 * the EntityCondition apoli:is_sprinting catches that
	 */
	public static ArrayList<Player> currentSprintingPlayersFallback = new ArrayList<>();

	public static void moveEquipmentInventory(Player player, EquipmentSlot equipmentSlot) {
		ItemStack item = player.getInventory().getItem(equipmentSlot);

		if (item != null && item.getType() != Material.AIR) {
			// Find an empty slot in the player's inventory
			int emptySlot = player.getInventory().firstEmpty();

			if (emptySlot != -1) {
				// Set the equipment slot to empty
				player.getInventory().setItem(equipmentSlot, null);

				// Move the item to the empty slot
				player.getInventory().setItem(emptySlot, item);
			}
		}
	}

	public static boolean hasOrigin(Player player, String originTag) {
		if (OriginDataContainer.getDataMap().containsKey(player)) {
			HashMap<Layer, Origin> origins = CraftApoli.toOrigin(OriginDataContainer.getLayer(player));
			for (Origin origin : origins.values()) if (origin.getTag().equals(originTag)) return true;
		}
		return false;
	}

	public static Origin getOrigin(Player player, Layer layer) {
		if (!OriginDataContainer.getDataMap().containsKey(player)) {
			if (OriginDataContainer.getLayer(player) == null) {
				setOrigin(player, layer, CraftApoli.emptyOrigin());
				return CraftApoli.emptyOrigin();
			}
		}
		return CraftApoli.toOrigin(OriginDataContainer.getLayer(player), layer);
	}

	public static HashMap<Layer, Origin> getOrigin(Player player) {
		return CraftApoli.toOrigin(OriginDataContainer.getLayer(player));
	}

	public static void setupPowers(Player p) {
		OriginDataContainer.loadData(p);
		String[] layers = OriginDataContainer.getLayer(p).split("\n");
		HashMap<Layer, ConcurrentLinkedQueue<Power>> map = new HashMap<>();
		for (String layer : layers) {
			String[] layerData = layer.split("\\|");
			Layer layerContainer = CraftApoli.getLayerFromTag(layerData[0]);
			ConcurrentLinkedQueue<Power> powers = new ConcurrentLinkedQueue<Power>();
			// setup powers
			for (String dataPiece : layerData) {
				if (layerData.length == 1) continue;
				Power powerCon = (Power) GenesisMC.getPlugin().registry.retrieve(Registries.POWER).get(NamespacedKey.fromString(dataPiece));
				if (powerCon != null) {
					if (powers.contains(powerCon)) continue;
					powers.add(powerCon);
					if (powerCon.isOriginMultipleParent()) {
						ArrayList<Power> nestedPowers = CraftApoli.getNestedPowers(powerCon);
						for (Power nested : nestedPowers) {
							if (nested != null) powers.add(nested);
						}
					}
				}
			}
			map.put(layerContainer, powers);
		}
		playerPowerMapping.put(p, map);
	}

	public static ArrayList<Power> getPowers(Player p, String powerType) {
		ArrayList<Power> powers = new ArrayList<>();
		if (playerPowerMapping.get(p) == null) return powers;
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			if (layer == null) continue;
			for (Power power : playerPowerMapping.get(p).get(layer)) {
				if (power == null) continue;
				if (power.getType().equals(powerType)) powers.add(power);
			}
		}
		return powers;
	}

	public static ArrayList<Power> getPowers(Player p, String powerType, Layer layer) {
		ArrayList<Power> powers = new ArrayList<>();
		if (playerPowerMapping.get(p) == null) return powers;
		for (Power power : playerPowerMapping.get(p).get(layer)) {
			if (power == null) continue;
			if (power.getType().equals(powerType)) powers.add(power);
		}
		return powers;
	}

	public static ArrayList<Power> getPowers(Player p) {
		ArrayList<Power> powers = new ArrayList<>();
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			for (Power power : playerPowerMapping.get(p).get(layer)) {
				if (power == null) continue;
				powers.add(power);
			}
		}
		return powers;
	}

	public static boolean hasPower(Player p, String powerKey) {
		if (playerPowerMapping.containsKey(p)) {
			for (Layer layerContainer : playerPowerMapping.get(p).keySet()) {
				for (Power power : playerPowerMapping.get(p).get(layerContainer)) {
					if (power.getTag().equalsIgnoreCase(powerKey)) return true;
				}
			}
		}
		return false;
	}

	public static void setOrigin(Player player, Layer layer, Origin origin) {
		NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originLayer");
		HashMap<Layer, Origin> origins = CraftApoli.toOrigin(player.getPersistentDataContainer().get(key, PersistentDataType.STRING));
		if (!CraftApoli.getLayersFromRegistry().contains(layer)) {
			return;
		}

		if (!origin.getTag().equals(CraftApoli.emptyOrigin().getTag())) {
			try {
				unassignPowers(player, layer);
			} catch (NotFoundException e) {
				throw new RuntimeException();
			}
		}
		for (Layer layers : origins.keySet()) {
			if (layer.getTag().equals(layers.getTag())) origins.replace(layers, origin);
		}
		player.getPersistentDataContainer().set(key, PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(origins));
		OriginDataContainer.loadData(player);
		setupPowers(player);

		String originTag = origin.getTag();
		if (!originTag.equals(CraftApoli.emptyOrigin().getTag())) Metrics.originPopularity(player);
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					assignPowers(player, layer);
					// Extra precaution due to gravity messing up on origin switch
					if (!new GravityPower().getPlayersWithPower().contains(player)) {
						new GravityPower().doesntHavePower(player);
					}
				} catch (InstantiationException | NotFoundException | SecurityException | NoSuchFieldException |
						 IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}.runTaskLater(GenesisMC.getPlugin(), 3L);
		inChoosingLayer.remove(player);
	}

	public static boolean isInPhantomForm(Player player) {
		return player.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform")) ? player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN) : false;
	}

	public static ConcurrentLinkedQueue<ApoliPower> getPowersApplied(Player p) {
		return powersAppliedList.get(p);
	}

	/**
	 * With how origins runs things, this needs to be in place
	 * because each power already ticks for multiple power instances
	 * and if a duplicate is found, then it needs to remove that power
	 * or the power could execute multiple times if its ticked
	 * inside the scheduler
	 *
	 * @param p
	 */
	public static void checkForDuplicates(Player p) {
		List<NamespacedKey> keys = new ArrayList<>();
		List<ApoliPower> duplicates = new ArrayList<>();
		for (ApoliPower power : getPowersApplied(p)) {
			if (keys.contains(power.getKey())) {
				duplicates.add(power);
			} else {
				keys.add(power.getKey());
			}
		}
		duplicates.forEach(power -> getPowersApplied(p).remove(power));
	}

	public static void applyPower(Player player, Power power, boolean suppress) {
		if (power == null) return;
		String name = power.getType().equalsIgnoreCase("apoli:simple") ? power.getTag() : power.getType();
		ApoliPower c = (ApoliPower) GenesisMC.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(NamespacedKey.fromString(name));
		if (c != null) {
			c.getPlayersWithPower().add(player);
			if (!powersAppliedList.containsKey(player))
				powersAppliedList.put(player, new ConcurrentLinkedQueue<ApoliPower>(List.of(c)));
			else powersAppliedList.get(player).add(c);
			if (!suppress) {
				if (OriginConfiguration.getConfiguration().getString("debug").equalsIgnoreCase("true"))
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Assigned power[" + power.getTag() + "] to player " + player.getName());
			}
			new PowerUpdateEvent(player, power, false).callEvent();
		}
	}

	public static void removePower(Player player, Power power, boolean suppress) {
		if (power == null) return;
		String name = power.getType().equalsIgnoreCase("apoli:simple") ? power.getTag() : power.getType();
		ApoliPower c = (ApoliPower) GenesisMC.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(NamespacedKey.fromString(name));
		if (c != null) {
			powersAppliedList.get(player).remove(c);
			c.getPlayersWithPower().remove(player);
			if (!suppress) {
				if (OriginConfiguration.getConfiguration().getString("debug").equalsIgnoreCase("true"))
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed power[" + power.getTag() + "] from player " + player.getName());
			}
			new PowerUpdateEvent(player, power, true).callEvent();
		}
	}

	public static void unassignPowers(@NotNull Player player) {
		HashMap<Layer, Origin> origins = getOrigin(player);
		for (Layer layer : origins.keySet()) {
			try {
				unassignPowers(player, layer);
				hasPowers.remove(player);
			} catch (NotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void assignPowers(@NotNull Player player) {
		HashMap<Layer, Origin> origins = getOrigin(player);
		for (Layer layer : origins.keySet()) {
			try {
				assignPowers(player, layer);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException |
					 SecurityException | NotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void unassignPowers(@NotNull Player player, Layer layer) throws NotFoundException {
		try {
			CompletableFuture.runAsync(() -> {
				if (layer == null) {
					GenesisMC.getPlugin().getLogger().severe("Provided layer was null! Was it removed? Skipping power application...");
					return;
				}
				for (Power power : playerPowerMapping.get(player).get(layer)) {
					removePower(player, power, false);
				}
			}).thenRun(() -> {
				OriginDataContainer.unloadData(player);
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public static void assignPowers(@NotNull Player player, Layer layer) throws InstantiationException, IllegalAccessException, NotFoundException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		try {
			CompletableFuture.runAsync(() -> {
				if (layer == null) {
					GenesisMC.getPlugin().getLogger().severe("Provided layer was null! Was it removed? Skipping power application...");
					return;
				}
				for (Power power : playerPowerMapping.get(player).get(layer)) {
					applyPower(player, power, false);
				}
			}).thenRun(() -> {
				OriginDataContainer.loadData(player);
				setupPowers(player);
				hasPowers.add(player);
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void sprint(PlayerToggleSprintEvent e) {
		if (e.isSprinting()) currentSprintingPlayersFallback.add(e.getPlayer());
		else currentSprintingPlayersFallback.remove(e.getPlayer());
	}
}
