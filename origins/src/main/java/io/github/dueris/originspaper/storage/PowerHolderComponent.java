package io.github.dueris.originspaper.storage;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.event.PowerUpdateEvent;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.type.MultiplePower;
import io.github.dueris.originspaper.util.entity.PowerUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class PowerHolderComponent implements Listener {
	public static LinkedList<Player> currentSprintingPlayersFallback = new LinkedList<>();

	private static ServerPlayer getNMS(Player player) {
		return ((CraftPlayer) player).getHandle();
	}

	@Unmodifiable
	public static <T extends PowerType> @NotNull List<T> getPowers(Entity entity, Class<T> typeOf) {
		if (!(entity instanceof Player player)) return List.of();
		return getPowers(player, (powerType) -> powerType.getClass().equals(typeOf));
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
		if (!(p instanceof Player player)) return new LinkedList<>();
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers();
	}

	@Unmodifiable
	public static @NotNull List<PowerType> getPowers(Entity p, OriginLayer layer) {
		if (!(p instanceof Player player)) return new LinkedList<>();
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers(layer);
	}

	public static @Nullable PowerType getPower(Entity p, ResourceLocation powerKey) {
		if (!(p instanceof Player player)) return null;
		return (PowerType) getPowers(player, (powerType) -> {
			return powerType.getTag().equalsIgnoreCase(powerKey.toString());
		}).stream().findFirst().orElse(null);
	}

	public static @NotNull List<PowerType> getPowersFromSource(OriginLayer layer, Entity entity) {
		if (!(entity instanceof Player player)) return new LinkedList<>();
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers(layer);
	}

	/**
	 * @return the power and all nested powers if its a multiple power instance
	 */
	public static @NotNull LinkedList<PowerType> getAllPowers(ResourceLocation location) {
		PowerType source = OriginsPaper.getPower(location);
		LinkedList<PowerType> powers = new LinkedList<>(List.of(source));
		if (source instanceof MultiplePower) {
			powers.addAll(getNestedPowerTypes(source));
		}

		return powers;
	}

	public static @NotNull LinkedList<PowerType> getNestedPowerTypes(PowerType power) {
		LinkedList<PowerType> nested = new LinkedList<>();
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

	public static boolean hasPower(Entity p, PowerType type) {
		if (!(p instanceof Player player)) return false;
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers().contains(type);
	}

	public static boolean hasPowerType(Entity p, ResourceLocation typeOf) {
		if (!(p instanceof Player player)) return false;
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers()
			.stream().map(PowerType::getType).toList().contains(typeOf.toString());
	}

	public static boolean hasPowerType(Entity p, Class<? extends PowerType> typeOf) {
		if (!(p instanceof Player player)) return false;
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers()
			.stream().map(PowerType::getClass).toList().contains(typeOf);
	}

	protected static void printNotFound(ResourceLocation location) {
		printNotFound(location, null);
	}

	protected static void printNotFound(@NotNull ResourceLocation location, @Nullable Origin origin) {
		OriginsPaper.LOGGER.error("Specified PowerType '{}'{} was not found in the registry.", location.toString(),
			origin == null ? "" : " in Origin '{}'".replace("{}", origin.getTag()));
	}

	public static @NotNull @Unmodifiable List<PowerType> getPowersApplied(Player p) {
		return PlayerPowerRepository.getOrCreateRepo(getNMS(p)).getAppliedPowers();
	}

	public static void checkForDuplicates(Player p) {
		PlayerPowerRepository repo = PlayerPowerRepository.getOrCreateRepo(getNMS(p));

		for (OriginLayer layer : OriginComponent.getLayers(p)) {
			List<ResourceLocation> keys = new LinkedList<>();
			List<PowerType> duplicates = new LinkedList<>();

			for (PowerType power : getPowersFromSource(layer, p)) {
				if (keys.contains(power.getId())) {
					duplicates.add(power);
				} else {
					keys.add(power.getId());
				}
			}

			duplicates.forEach(powerx -> {
				repo.removePower(powerx, layer);
			});
		}
	}

	public static boolean isOfType(@NotNull PowerType type, Class<? extends PowerType> typeOf) {
		return type.getClass().equals(typeOf);
	}

	public static void loadPower(Player player, PowerType power, OriginLayer layer) {
		loadPower(player, power, layer, false);
	}

	public static void loadPower(Player player, PowerType power, OriginLayer layer, boolean isNew) {
		if (power != null) {
			power.forPlayer(((CraftPlayer) player).getHandle());
			PlayerPowerRepository.getOrCreateRepo(getNMS(player)).addPower(power, layer);

			if (isNew) {
				power.onAdded(((CraftPlayer) player).getHandle());
			}
			PowerUtils.markGained(power, player);

			new PowerUpdateEvent(player, power, false, isNew).callEvent();
		}
	}

	public static void unloadPower(Player player, PowerType power, OriginLayer layer) {
		unloadPower(player, power, layer, false);
	}

	public static void unloadPower(Player player, PowerType power, OriginLayer layer, boolean isNew) {
		if (power != null) {
			if (isNew) {
				power.onRemoved(((CraftPlayer) player).getHandle());
			}
			PlayerPowerRepository.getOrCreateRepo(getNMS(player)).removePower(power, layer);
			power.removePlayer(((CraftPlayer) player).getHandle());

			new PowerUpdateEvent(player, power, true, isNew).callEvent();
		}
	}

	public static void unloadPowers(@NotNull Player player) {
		for (OriginLayer layer : OriginComponent.getLayers(player)) {
			unloadPowers(player, layer);
		}
	}

	public static void loadPowers(@NotNull Player player) {
		for (OriginLayer layer : OriginComponent.getLayers(player)) {
			loadPowers(player, layer);
		}
	}

	public static void unloadPowers(@NotNull Player player, OriginLayer layer) {
		unloadPowers(player, layer, false);
	}

	public static void unloadPowers(@NotNull Player player, OriginLayer layer, boolean isNew) {
		if (layer == null) {
			OriginsPaper.LOGGER.error("Provided layer was null! Was it removed? Skipping power application...");
			return;
		}

		for (PowerType power : PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers(layer)) {
			unloadPower(player, power, layer, isNew);
		}
	}

	public static void loadPowers(@NotNull Player player, OriginLayer layer) {
		loadPowers(player, layer, false);
	}

	public static void loadPowers(@NotNull Player player, OriginLayer layer, boolean isNew) {
		if (layer == null) {
			OriginsPaper.LOGGER.error("Provided layer was null! Was it removed? Skipping power application...");
			return;
		}

		for (PowerType power : PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers(layer)) {
			loadPower(player, power, layer, isNew);
		}
	}

	public static <T extends PowerType> @NotNull List<T> gatherConditionedPowers(Entity p, Class<T> type, Predicate<T> predicate) {
		LinkedList<T> powers = new LinkedList<>();
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

	public static @NotNull List<ResourceLocation> getSources(PowerType power, Entity entity) {
		if (!(entity instanceof Player player)) return new LinkedList<>();
		List<ResourceLocation> locations = new LinkedList<>();

		PlayerPowerRepository repo = PlayerPowerRepository.getOrCreateRepo(getNMS(player));
		for (OriginLayer layer : repo.getLayers()) {

			if (repo.getAppliedPowers(layer).contains(power)) {
				locations.add(layer.getId());
			}
		}

		return locations;
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
