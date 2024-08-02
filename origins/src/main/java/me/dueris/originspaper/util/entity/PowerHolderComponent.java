package me.dueris.originspaper.util.entity;

import javassist.NotFoundException;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.OriginChangeEvent;
import me.dueris.originspaper.event.PowerUpdateEvent;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.Origin;
import me.dueris.originspaper.registry.registries.OriginLayer;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.screen.ScreenNavigator;
import me.dueris.originspaper.storage.OriginDataContainer;
import me.dueris.originspaper.util.BstatsMetrics;
import me.dueris.originspaper.util.exception.PowerNotFoundException;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PowerHolderComponent implements Listener {
	public static ConcurrentHashMap<Player, ConcurrentHashMap<OriginLayer, ConcurrentLinkedQueue<PowerType>>> playerPowerMapping = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<Player, ConcurrentLinkedQueue<PowerType>> powersAppliedList = new ConcurrentHashMap<>();
	public static ConcurrentLinkedQueue<Player> hasPowers = new ConcurrentLinkedQueue<>();
	public static ArrayList<Player> currentSprintingPlayersFallback = new ArrayList<>();

	public static void moveEquipmentInventory(@NotNull Player player, EquipmentSlot equipmentSlot) {
		ItemStack item = player.getInventory().getItem(equipmentSlot);
		if (item != null && item.getType() != Material.AIR) {
			int emptySlot = player.getInventory().firstEmpty();
			if (emptySlot != -1) {
				player.getInventory().setItem(equipmentSlot, null);
				player.getInventory().setItem(emptySlot, item);
			}
		}
	}

	public static boolean hasOrigin(Player player, String originTag) {
		if (OriginDataContainer.getDataMap().containsKey(player)) {
			HashMap<OriginLayer, Origin> origins = CraftApoli.toOrigin(OriginDataContainer.getLayer(player));

			for (Origin origin : origins.values()) {
				if (origin.getTag().equals(originTag)) {
					return true;
				}
			}
		}

		return false;
	}

	public static Origin getOrigin(Player player, OriginLayer layer) {
		if (!OriginDataContainer.getDataMap().containsKey(player) && OriginDataContainer.getLayer(player) == null) {
			setOrigin(player, layer, CraftApoli.emptyOrigin());
			return CraftApoli.emptyOrigin();
		} else {
			return CraftApoli.toOrigin(OriginDataContainer.getLayer(player), layer);
		}
	}

	public static HashMap<OriginLayer, Origin> getOrigin(Player player) {
		return CraftApoli.toOrigin(OriginDataContainer.getLayer(player));
	}

	public static void setupPowers(Player p) {
		OriginDataContainer.loadData(p);
		String[] layers = OriginDataContainer.getLayer(p).split("\n");
		ConcurrentHashMap<OriginLayer, ConcurrentLinkedQueue<PowerType>> map = new ConcurrentHashMap<>();

		for (String layer : layers) {
			String[] layerData = layer.split("\\|");
			OriginLayer layerContainer = CraftApoli.getLayer(layerData[0]);
			ConcurrentLinkedQueue<PowerType> powers = new ConcurrentLinkedQueue<>();

			for (String dataPiece : layerData) {
				if (layerData.length == 1) continue;
				PowerType powerCon = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(ResourceLocation.parse(dataPiece));
				if (powerCon != null) {
					if (powers.contains(powerCon)) continue;
					powers.add(powerCon);
//					if (powerCon.getClass().equals(Multiple.class)) {
//						ArrayList<PowerType> nestedPowers = CraftApoli.getNestedPowerTypes(powerCon);
//						for (PowerType nested : nestedPowers) {
//							if (nested != null) powers.add(nested);
//						}
//					}
				}
			}
			map.put(layerContainer, powers);
		}

		playerPowerMapping.put(p, map);
	}

	public static <T extends PowerType> @NotNull ArrayList<T> getPowers(Entity p, Class<T> typeOf) {
		ArrayList<T> powers = new ArrayList<>();
		if (!(p instanceof Player)) return powers;
		if (playerPowerMapping.get(p) == null) return powers;
		for (OriginLayer layer : CraftApoli.getLayersFromRegistry()) {
			if (layer == null) continue;
			for (PowerType power : playerPowerMapping.get(p).get(layer)) {
				if (power == null || !power.getClass().equals(typeOf)) continue;
				powers.add(typeOf.cast(power));
			}
		}
		return powers;
	}

	public static @NotNull ArrayList<PowerType> getPowers(Player p, String typeOf) {
		ArrayList<PowerType> powers = new ArrayList<>();
		if (playerPowerMapping.get(p) == null) return powers;
		for (OriginLayer layer : CraftApoli.getLayersFromRegistry()) {
			if (layer == null) continue;
			for (PowerType power : playerPowerMapping.get(p).get(layer)) {
				if (power == null || !power.getType().equalsIgnoreCase(typeOf)) continue;
				powers.add(power);
			}
		}
		return powers;
	}

	@Contract("null -> new")
	public static @NotNull ArrayList<PowerType> getPowers(Entity p) {
		if (!(p instanceof Player)) return new ArrayList<>();
		ArrayList<PowerType> powers = new ArrayList<>();
		for (OriginLayer layer : CraftApoli.getLayersFromRegistry()) {
			for (PowerType power : playerPowerMapping.get(p).get(layer)) {
				if (power == null) continue;
				powers.add(power);
			}
		}
		return powers;
	}

	public static PowerType getPower(Entity p, String powerKey) {
		if (!(p instanceof Player)) return null;
		for (OriginLayer layerContainer : playerPowerMapping.get(p).keySet()) {
			for (PowerType power : playerPowerMapping.get(p).get(layerContainer)) {
				if (power.getTag().equalsIgnoreCase(powerKey)) return power;
			}
		}
		return null;
	}

	public static boolean hasPower(Entity p, String powerKey) {
		if (!(p instanceof Player) || playerPowerMapping.get(p) == null) return false;
		for (OriginLayer layerContainer : playerPowerMapping.get(p).keySet()) {
			for (PowerType power : playerPowerMapping.get(p).get(layerContainer)) {
				if (power.getTag().equalsIgnoreCase(powerKey)) return true;
			}
		}
		return false;
	}

	public static boolean hasPowerType(Entity p, Class<? extends PowerType> typeOf) {
		if (!(p instanceof Player)) return false;
		for (OriginLayer layerContainer : playerPowerMapping.get(p).keySet()) {
			for (PowerType power : playerPowerMapping.get(p).get(layerContainer)) {
				if (power.getClass().equals(typeOf)) return true;
			}
		}
		return false;
	}

	public static void setOrigin(final @NotNull Entity entity, final OriginLayer layer, final Origin origin) {
		if (!(entity instanceof Player player)) return;
		NamespacedKey key = new NamespacedKey(OriginsPaper.getPlugin(), "originLayer");
		HashMap<OriginLayer, Origin> origins = CraftApoli.toOrigin(player.getPersistentDataContainer().get(key, PersistentDataType.STRING));
		if (CraftApoli.getLayersFromRegistry().contains(layer)) {
			if (!origin.getTag().equals(CraftApoli.emptyOrigin().getTag())) {
				try {
					unassignPowers(player, layer, true);
				} catch (NotFoundException var7) {
					throw new RuntimeException();
				}
			}

			for (OriginLayer layers : origins.keySet()) {
				if (layer.getTag().equals(layers.getTag())) {
					origins.replace(layers, origin);
				}
			}

			player.getPersistentDataContainer().set(key, PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(origins));
			OriginDataContainer.loadData(player);
			setupPowers(player);
			String originTag = origin.getTag();
			if (!originTag.equals(CraftApoli.emptyOrigin().getTag())) {
				BstatsMetrics.originPopularity(player);
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						PowerHolderComponent.assignPowers(player, layer, true);
					} catch (NotFoundException | SecurityException | NoSuchFieldException | IllegalArgumentException |
							 IllegalAccessException | InstantiationException var2) {
						throw new RuntimeException(var2);
					}

					OriginChangeEvent e = new OriginChangeEvent(player, origin, ScreenNavigator.orbChoosing.contains(player));
					Bukkit.getPluginManager().callEvent(e);
				}
			}
				.runTaskLater(OriginsPaper.getPlugin(), 3L);
			ScreenNavigator.inChoosingLayer.remove(player);
		}
	}

	public static boolean isInPhantomForm(@NotNull Player player) {
		return player.getPersistentDataContainer().has(CraftNamespacedKey.fromString("originspaper:in-phantomform"))
			? player.getPersistentDataContainer().get(CraftNamespacedKey.fromString("originspaper:in-phantomform"), PersistentDataType.BOOLEAN)
			: false;
	}

	public static ConcurrentLinkedQueue<PowerType> getPowersApplied(Player p) {
		return powersAppliedList.getOrDefault(p, new ConcurrentLinkedQueue<>());
	}

	public static void checkForDuplicates(Player p) {
		List<ResourceLocation> keys = new ArrayList<>();
		List<PowerType> duplicates = new ArrayList<>();

		for (PowerType power : getPowersApplied(p)) {
			if (keys.contains(power.key())) {
				duplicates.add(power);
			} else {
				keys.add(power.key());
			}
		}

		duplicates.forEach(powerx -> getPowersApplied(p).remove(powerx));
	}

	@Contract("_, null -> false")
	public static boolean isOfType(@NotNull PowerType type, Class<? extends PowerType> typeOf) {
		return type.getClass().equals(typeOf);
	}

	public static void applyPower(Player player, PowerType power, boolean suppress) {
		applyPower(player, power, suppress, false);
	}

	public static void applyPower(Player player, PowerType power, boolean suppress, boolean isNew) {
		if (power != null) {
			ResourceLocation registryKey = power.key();
			PowerType c = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(registryKey);
			if (c != null) {
				c.forPlayer(((CraftPlayer) player).getHandle());
				if (!powersAppliedList.containsKey(player)) {
					powersAppliedList.put(player, new ConcurrentLinkedQueue<>(List.of(c)));
				} else {
					powersAppliedList.get(player).add(c);
				}

				c.bootstrapApply(((CraftPlayer) player).getHandle());
				if (!suppress) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Assigned power[" + power.getTag() + "] to player " + player.getName());
				}

				new PowerUpdateEvent(player, power, false, isNew).callEvent();
			} else {
				throw new PowerNotFoundException(registryKey.toString());
			}
		}
	}

	public static void removePower(Player player, PowerType power, boolean suppress) {
		removePower(player, power, suppress, false);
	}

	public static void removePower(Player player, PowerType power, boolean suppress, boolean isNew) {
		if (power != null) {
			ResourceLocation registryKey = power.key();
			PowerType c = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(registryKey);
			if (c != null) {
				c.bootstrapUnapply(((CraftPlayer) player).getHandle());
				powersAppliedList.get(player).remove(c);
				c.removePlayer(((CraftPlayer) player).getHandle());
				if (!suppress) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed power[" + power.getTag() + "] from player " + player.getName());
				}

				new PowerUpdateEvent(player, power, true, isNew).callEvent();
			} else {
				throw new PowerNotFoundException(registryKey.toString());
			}
		}
	}

	public static void unassignPowers(@NotNull Player player) {
		HashMap<OriginLayer, Origin> origins = getOrigin(player);

		for (OriginLayer layer : origins.keySet()) {
			try {
				unassignPowers(player, layer);
				hasPowers.remove(player);
			} catch (NotFoundException var5) {
				throw new RuntimeException(var5);
			}
		}
	}

	public static void assignPowers(@NotNull Player player) {
		HashMap<OriginLayer, Origin> origins = getOrigin(player);

		for (OriginLayer layer : origins.keySet()) {
			try {
				assignPowers(player, layer);
			} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException |
					 NotFoundException | InstantiationException var5) {
				throw new RuntimeException(var5);
			}
		}
	}

	public static void unassignPowers(@NotNull Player player, OriginLayer layer) throws NotFoundException {
		unassignPowers(player, layer, false);
	}

	public static void unassignPowers(@NotNull Player player, OriginLayer layer, boolean isNew) throws NotFoundException {
		try {
			if (layer == null) {
				OriginsPaper.getPlugin().getLogger().severe("Provided layer was null! Was it removed? Skipping power application...");
				return;
			}

			for (PowerType power : playerPowerMapping.get(player).get(layer)) {
				removePower(player, power, false);
			}

			OriginDataContainer.unloadData(player);
		} catch (Throwable var5) {
			var5.printStackTrace();
		}
	}

	public static void assignPowers(@NotNull Player player, OriginLayer layer) throws InstantiationException, IllegalAccessException, NotFoundException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		assignPowers(player, layer, false);
	}

	public static void assignPowers(@NotNull Player player, OriginLayer layer, boolean isNew) throws InstantiationException, IllegalAccessException, NotFoundException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		try {
			if (layer == null) {
				OriginsPaper.getPlugin().getLogger().severe("Provided layer was null! Was it removed? Skipping power application...");
				return;
			}

			for (PowerType power : playerPowerMapping.get(player).get(layer)) {
				applyPower(player, power, false, isNew);
			}

			OriginDataContainer.loadData(player);
			setupPowers(player);
			hasPowers.add(player);
		} catch (Throwable var5) {
			var5.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void sprint(@NotNull PlayerToggleSprintEvent e) {
		if (e.isSprinting()) {
			currentSprintingPlayersFallback.add(e.getPlayer());
		} else {
			currentSprintingPlayersFallback.remove(e.getPlayer());
		}
	}
}
