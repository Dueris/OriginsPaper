package me.dueris.originspaper.storage;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.OriginChangeEvent;
import me.dueris.originspaper.event.PowerUpdateEvent;
import me.dueris.originspaper.origin.Origin;
import me.dueris.originspaper.origin.OriginLayer;
import me.dueris.originspaper.power.MultiplePower;
import me.dueris.originspaper.power.PowerType;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.screen.ScreenNavigator;
import me.dueris.originspaper.util.BstatsMetrics;
import me.dueris.originspaper.util.entity.PowerUtils;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PowerHolderComponent implements Listener {
	public static ArrayList<Player> currentSprintingPlayersFallback = new ArrayList<>();

	private static ServerPlayer getNMS(Player player) {
		return ((CraftPlayer) player).getHandle();
	}

	public static void moveEquipmentInventory(@NotNull Player player, EquipmentSlot equipmentSlot) {
		ItemStack item = player.getInventory().getItem(equipmentSlot);
		if (item != null && item.getType() != Material.AIR) {
			int emptySlot = player.getInventory().firstEmpty();
			if (emptySlot != -1) {
				player.getInventory().setItem(equipmentSlot, null);
				player.getInventory().setItem(emptySlot, item);
			} else {
				player.getWorld().dropItem(player.getLocation(), item);
				player.getInventory().setItem(equipmentSlot, null);
			}
		}
	}

	public static boolean hasOrigin(Player player, String originTag) {
		Origin origin = OriginsPaper.getPlugin().registry.retrieve(Registries.ORIGIN).get(ResourceLocation.parse(originTag));
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).origins.containsValue(origin);
	}

	public static Origin getOrigin(Player player, OriginLayer layer) {
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).origins.getOrDefault(layer, OriginsPaper.EMPTY_ORIGIN);
	}

	public static Map<OriginLayer, Origin> getOrigin(Player player) {
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).origins;
	}

	@Unmodifiable
	public static <T extends PowerType> @NotNull List<T> getPowers(Entity entity, Class<T> typeOf) {
		if (!(entity instanceof Player player)) return List.of();
		return getPowers(player, (powerType) -> powerType.getClass().equals(typeOf));
	}

	@Unmodifiable
	public static @NotNull List<PowerType> getPowers(Player p, String typeOf) {
		return getPowers(p, (powerType) -> powerType.getType().equalsIgnoreCase(typeOf));
	}

	@Unmodifiable
	@SuppressWarnings("unchecked")
	public static <T> @NotNull List<T> getPowers(Player player, Predicate<PowerType> powerTypePredicate) {
		List<PowerType> powers = PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers();
		return (List<T>) powers.stream()
			.filter(powerTypePredicate).toList();
	}

	@Unmodifiable
	public static @NotNull List<PowerType> getPowers(Entity p) {
		if (!(p instanceof Player player)) return new ArrayList<>();
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers();
	}

	public static @Nullable PowerType getPower(Entity p, String powerKey) {
		if (!(p instanceof Player player)) return null;
		return (PowerType) getPowers(player, (powerType) -> {
			return powerType.getTag().equalsIgnoreCase(powerKey);
		}).stream().findFirst().orElseThrow();
	}

	public static @NotNull ArrayList<PowerType> getNestedPowerTypes(PowerType power) {
		ArrayList<PowerType> nested = new ArrayList<>();
		if (power != null) {
			if (power instanceof MultiplePower multiple) {
				nested.addAll(multiple.getSubPowers());
			}

		}
		return nested;
	}

	public static boolean hasPower(Entity p, String powerKey) {
		if (!(p instanceof Player player)) return false;
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers()
			.stream().map(PowerType::getTag).toList().contains(powerKey);
	}

	public static boolean hasPowerType(Entity p, Class<? extends PowerType> typeOf) {
		if (!(p instanceof Player player)) return false;
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers()
			.stream().map(PowerType::getClass).toList().contains(typeOf);
	}

	public static void setOrigin(final @NotNull Entity entity, final OriginLayer layer, final Origin origin) {
		if (!(entity instanceof Player player)) return;
		Map<OriginLayer, Origin> origins = getOrigin(player);
		if (OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER).values().contains(layer)) {
			if (!origin.getTag().equals(OriginsPaper.EMPTY_ORIGIN.getTag())) {
				unassignPowers(player, layer);
			}

			for (OriginLayer layers : origins.keySet()) {
				if (layer.getTag().equals(layers.getTag())) {
					origins.replace(layers, origin);
				}
			}

			String originTag = origin.getTag();
			if (!originTag.equals(OriginsPaper.EMPTY_ORIGIN.getTag())) {
				BstatsMetrics.originPopularity(player);
			}

			PowerHolderComponent.assignPowers(player, layer, origin, true);

			OriginChangeEvent e = new OriginChangeEvent(player, origin, ScreenNavigator.orbChoosing.contains(getNMS(player)));
			Bukkit.getPluginManager().callEvent(e);
			ScreenNavigator.inChoosingLayer.remove(getNMS(player));
		}
	}

	public static List<PowerType> getPowersApplied(Player p) {
		return PlayerPowerRepository.getOrCreateRepo(getNMS(p)).getAppliedPowers();
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

	public static boolean isOfType(@NotNull PowerType type, Class<? extends PowerType> typeOf) {
		return type.getClass().equals(typeOf);
	}

	public static void applyPower(Player player, PowerType power, OriginLayer layer, boolean suppress) {
		applyPower(player, power, layer, suppress, false);
	}

	public static void applyPower(Player player, PowerType power, OriginLayer layer, boolean suppress, boolean isNew) {
		if (power != null) {
			power.forPlayer(((CraftPlayer) player).getHandle());
			PlayerPowerRepository.getOrCreateRepo(getNMS(player)).addPower(power, layer);

			power.onAdded(((CraftPlayer) player).getHandle());
			PowerUtils.markGained(power, player);
			if (!suppress) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Assigned power[" + power.getTag() + "] to player " + player.getName());
			}

			new PowerUpdateEvent(player, power, false, isNew).callEvent();
		}
	}

	public static void removePower(Player player, PowerType power, OriginLayer layer, boolean suppress) {
		removePower(player, power, layer, suppress, false);
	}

	public static void removePower(Player player, PowerType power, OriginLayer layer, boolean suppress, boolean isNew) {
		if (power != null) {
			power.onRemoved(((CraftPlayer) player).getHandle());
			PlayerPowerRepository.getOrCreateRepo(getNMS(player)).removePower(power, layer);
			power.removePlayer(((CraftPlayer) player).getHandle());
			if (!suppress) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed power[" + power.getTag() + "] from player " + player.getName());
			}

			new PowerUpdateEvent(player, power, true, isNew).callEvent();
		}
	}

	public static void unassignPowers(@NotNull Player player) {
		Map<OriginLayer, Origin> origins = getOrigin(player);

		for (OriginLayer layer : origins.keySet()) {
			unassignPowers(player, layer);
		}
	}

	public static void assignPowers(@NotNull Player player) {
		Map<OriginLayer, Origin> origins = getOrigin(player);

		for (OriginLayer layer : origins.keySet()) {
			assignPowers(player, layer, origins.get(layer));
		}
	}

	public static void unassignPowers(@NotNull Player player, OriginLayer layer) {
		if (layer == null) {
			OriginsPaper.getPlugin().getLogger().severe("Provided layer was null! Was it removed? Skipping power application...");
			return;
		}

		for (PowerType power : PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers()) {
			removePower(player, power, layer, false);
		}
	}

	public static void assignPowers(@NotNull Player player, OriginLayer layer, Origin origin) {
		assignPowers(player, layer, origin, false);
	}

	public static void assignPowers(@NotNull Player player, OriginLayer layer, Origin origin, boolean isNew) {
		if (layer == null) {
			OriginsPaper.getPlugin().getLogger().severe("Provided layer was null! Was it removed? Skipping power application...");
			return;
		}

		for (PowerType power : origin.powers().stream().map(OriginsPaper::getPower).toList()) {
			applyPower(player, power, layer, false, isNew);
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
