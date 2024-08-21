package io.github.dueris.originspaper.storage;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.event.OriginChangeEvent;
import io.github.dueris.originspaper.event.PowerUpdateEvent;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.MultiplePower;
import io.github.dueris.originspaper.power.PowerType;
import io.github.dueris.originspaper.power.SimplePower;
import io.github.dueris.originspaper.power.provider.OriginSimpleContainer;
import io.github.dueris.originspaper.power.provider.PowerProvider;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.screen.ScreenNavigator;
import io.github.dueris.originspaper.util.BstatsMetrics;
import io.github.dueris.originspaper.util.entity.PowerUtils;
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
import java.util.Objects;
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
			.filter(Objects::nonNull).filter(powerTypePredicate).toList();
	}

	@Unmodifiable
	public static @NotNull List<PowerType> getPowers(Entity p) {
		if (!(p instanceof Player player)) return new ArrayList<>();
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers();
	}

	@Unmodifiable
	public static @NotNull List<PowerType> getPowers(Entity p, OriginLayer layer) {
		if (!(p instanceof Player player)) return new ArrayList<>();
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers(layer);
	}

	public static @Nullable PowerType getPower(Entity p, String powerKey) {
		if (!(p instanceof Player player)) return null;
		return (PowerType) getPowers(player, (powerType) -> {
			return powerType.getTag().equalsIgnoreCase(powerKey);
		}).stream().findFirst().orElse(null);
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
				unloadPowers(player, layer);
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

			PlayerPowerRepository repository = PlayerPowerRepository.getOrCreateRepo(getNMS(player));
			for (ResourceLocation power : origin.powers()) {
				PowerType rootPower = OriginsPaper.getPower(power);
				if (rootPower == null) {
					printNotFound(power, origin);
					continue;
				}
				List<PowerType> types = new ArrayList<>(List.of(rootPower));
				if (rootPower instanceof MultiplePower multiplePower) {
					types.addAll(multiplePower.getSubPowers());
				}

				for (PowerType powerType : types) {
					if (powerType == null) {
						printNotFound(power, origin);
						continue;
					}
					repository.addPower(powerType, layer);
				}
			}

			repository.origins.put(layer, origin);

			PowerHolderComponent.loadPowers(player, layer, true);

			OriginChangeEvent e = new OriginChangeEvent(player, origin, layer, ScreenNavigator.orbChoosing.contains(getNMS(player)));
			Bukkit.getPluginManager().callEvent(e);
			ScreenNavigator.inChoosingLayer.remove(getNMS(player));
		}
	}

	private static void printNotFound(ResourceLocation location) {
		printNotFound(location, null);
	}

	private static void printNotFound(@NotNull ResourceLocation location, @Nullable Origin origin) {
		OriginsPaper.getPlugin().getLog4JLogger().error("Specified PowerType '{}'{} was not found in the registry.", location.toString(),
			origin == null ? "" : " in Origin '{}'".replace("{}", origin.getTag()));
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

	public static void loadPower(Player player, PowerType power, OriginLayer layer, boolean suppress) {
		loadPower(player, power, layer, suppress, false);
	}

	public static void loadPower(Player player, PowerType power, OriginLayer layer, boolean suppress, boolean isNew) {
		if (power != null) {
			power.forPlayer(((CraftPlayer) player).getHandle());
			PlayerPowerRepository.getOrCreateRepo(getNMS(player)).addPower(power, layer);

			if (isNew) {
				power.onAdded(((CraftPlayer) player).getHandle());
			}
			PowerUtils.markGained(power, player);
			if (!suppress) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Assigned power[" + power.getTag() + "] to player " + player.getName());
			}

			new PowerUpdateEvent(player, power, false, isNew).callEvent();
		}
	}

	public static void unloadPower(Player player, PowerType power, OriginLayer layer, boolean suppress) {
		unloadPower(player, power, layer, suppress, false);
	}

	public static void unloadPower(Player player, PowerType power, OriginLayer layer, boolean suppress, boolean isNew) {
		if (power != null) {
			if (isNew) {
				power.onRemoved(((CraftPlayer) player).getHandle());

				if (power instanceof SimplePower simplePower) {
					PowerProvider provider = OriginSimpleContainer.getFromSimple(simplePower);
					provider.onRemove(player);
				}
			}
			PlayerPowerRepository.getOrCreateRepo(getNMS(player)).removePower(power, layer);
			power.removePlayer(((CraftPlayer) player).getHandle());
			if (!suppress) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed power[" + power.getTag() + "] from player " + player.getName());
			}

			new PowerUpdateEvent(player, power, true, isNew).callEvent();
		}
	}

	public static void unloadPowers(@NotNull Player player) {
		Map<OriginLayer, Origin> origins = getOrigin(player);

		for (OriginLayer layer : origins.keySet()) {
			unloadPowers(player, layer);
		}
	}

	public static void loadPowers(@NotNull Player player) {
		Map<OriginLayer, Origin> origins = getOrigin(player);

		for (OriginLayer layer : origins.keySet()) {
			loadPowers(player, layer);
		}
	}

	public static void unloadPowers(@NotNull Player player, OriginLayer layer) {
		if (layer == null) {
			OriginsPaper.getPlugin().getLogger().severe("Provided layer was null! Was it removed? Skipping power application...");
			return;
		}

		for (PowerType power : PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers(layer)) {
			unloadPower(player, power, layer, false);
		}
	}

	public static void loadPowers(@NotNull Player player, OriginLayer layer) {
		loadPowers(player, layer, false);
	}

	public static void loadPowers(@NotNull Player player, OriginLayer layer, boolean isNew) {
		if (layer == null) {
			OriginsPaper.getPlugin().getLogger().severe("Provided layer was null! Was it removed? Skipping power application...");
			return;
		}

		for (PowerType power : PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers(layer)) {
			loadPower(player, power, layer, false, isNew);
		}
	}

	public static <T extends PowerType> List<T> gatherConditionedPowers(Entity p, Class<T> type, Predicate<T> predicate) {
		ArrayList<T> powers = new ArrayList<>();
		if (hasPowerType(p, type)) {
			for (T power : getPowers(p, type)) {
				if (predicate.test(power)) {
					powers.add(power);
				}
			}
		}
		return powers;
	}

	public static <T extends PowerType> boolean doesHaveConditionedPower(Entity p, Class<T> type, Predicate<T> predicate) {
		boolean pass = false;
		if (hasPowerType(p, type)) {
			for (T power : getPowers(p, type)) {
				if (predicate.test(power)) {
					pass = true;
				}
			}
		}
		return pass;
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
