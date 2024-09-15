package io.github.dueris.originspaper.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import io.github.dueris.originspaper.event.PowerUpdateEvent;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.type.AttributeModifyTransferPower;
import io.github.dueris.originspaper.power.type.ModifierPower;
import io.github.dueris.originspaper.power.type.MultiplePower;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import io.github.dueris.originspaper.util.PowerUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PowerHolderComponent implements Listener {
	public static final Codec<ResourceLocation> VALIDATING_CODEC = ResourceLocation.CODEC.comapFlatMap(
		id -> ApoliRegistries.POWER.containsKey(id)
			? DataResult.success(id)
			: DataResult.error(() -> "Couldn't get power from ID \"" + id + "\", as it wasn't registered!"),
		Function.identity()
	);

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
	public static @NotNull List<PowerType> getPowers(Entity p, ResourceLocation layer) {
		if (!(p instanceof Player player)) return new LinkedList<>();
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers(layer);
	}

	public static @Nullable PowerType getPower(Entity p, ResourceLocation powerKey) {
		if (!(p instanceof Player player)) return null;
		return (PowerType) getPowers(player, (powerType) -> {
			return powerType.getTag().equalsIgnoreCase(powerKey.toString());
		}).stream().findFirst().orElse(null);
	}

	public static @NotNull List<PowerType> getPowersFromSource(ResourceLocation source, Entity entity) {
		if (!(entity instanceof Player player)) return new LinkedList<>();
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers(source);
	}

	public static void forEachTickable(Entity entity, Consumer<PowerType> typeConsumer) {
		if (!(entity instanceof Player player)) return;
		for (PowerType powerType : getPowersApplied(player)) {
			if (powerType.shouldTick()) {
				typeConsumer.accept(powerType);
			}
		}
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

		for (ResourceLocation location : repo.getPowerSources()) {
			List<ResourceLocation> keys = new LinkedList<>();
			List<PowerType> duplicates = new LinkedList<>();

			for (PowerType power : getPowersFromSource(location, p)) {
				if (keys.contains(power.getId())) {
					duplicates.add(power);
				} else {
					keys.add(power.getId());
				}
			}

			duplicates.forEach(powerx -> {
				repo.removePower(powerx, location);
			});
		}
	}

	public static boolean isOfType(@NotNull PowerType type, Class<? extends PowerType> typeOf) {
		return type.getClass().equals(typeOf);
	}

	public static void loadPower(Player player, PowerType power, ResourceLocation layer) {
		loadPower(player, power, layer, false);
	}

	public static void loadPower(Player player, PowerType power, ResourceLocation layer, boolean isNew) {
		if (power != null) {
			power.forPlayer(((CraftPlayer) player).getHandle());
			PlayerPowerRepository repository = PlayerPowerRepository.getOrCreateRepo(getNMS(player));
			repository.addPower(power, layer);
			if (power instanceof MultiplePower multiplePower) {
				for (PowerType subPower : multiplePower.getSubPowers()) {
					if (subPower != null) {
						repository.addPower(subPower, layer);
					}
				}
			}

			if (isNew) {
				power.onAdded(((CraftPlayer) player).getHandle());
			}
			PowerUtils.markGained(power, player);

			new PowerUpdateEvent(player, power, false, isNew).callEvent();
		}
	}

	public static void unloadPower(Player player, PowerType power, ResourceLocation layer) {
		unloadPower(player, power, layer, false);
	}

	public static void unloadPower(Player player, PowerType power, ResourceLocation layer, boolean isNew) {
		if (power != null) {
			if (isNew) {
				power.onRemoved(((CraftPlayer) player).getHandle());
			}
			PlayerPowerRepository repository = PlayerPowerRepository.getOrCreateRepo(getNMS(player));
			repository.removePower(power, layer);
			if (power instanceof MultiplePower multiplePower) {
				for (PowerType subPower : multiplePower.getSubPowers()) {
					if (subPower != null) {
						repository.removePower(subPower, layer);
					}
				}
			}
			power.removePlayer(((CraftPlayer) player).getHandle());

			new PowerUpdateEvent(player, power, true, isNew).callEvent();
		}
	}

	public static void unloadPowers(@NotNull Player player) {
		for (ResourceLocation location : PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getPowerSources()) {
			unloadPowers(player, location);
		}
	}

	public static void loadPowers(@NotNull Player player) {
		for (ResourceLocation location : PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getPowerSources()) {
			loadPowers(player, location);
		}
	}

	public static void unloadPowers(@NotNull Player player, ResourceLocation layer) {
		unloadPowers(player, layer, false);
	}

	public static void unloadPowers(@NotNull Player player, ResourceLocation layer, boolean isNew) {
		if (layer == null) {
			OriginsPaper.LOGGER.error("Provided layer was null! Was it removed? Skipping power application...");
			return;
		}

		for (PowerType power : PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAppliedPowers(layer)) {
			unloadPower(player, power, layer, isNew);
		}
	}

	public static void loadPowers(@NotNull Player player, ResourceLocation layer) {
		loadPowers(player, layer, false);
	}

	public static void loadPowers(@NotNull Player player, ResourceLocation layer, boolean isNew) {
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
		for (ResourceLocation location : PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getPowerSources()) {

			if (repo.getAppliedPowers(location).contains(power)) {
				locations.add(location);
			}
		}

		return locations;
	}

	public static <T extends ModifierPower> float modify(Entity entity, Class<T> powerClass, float baseValue) {
		return (float) modify(entity, powerClass, (double) baseValue, null, null);
	}

	public static <T extends ModifierPower> float modify(Entity entity, Class<T> powerClass, float baseValue, Predicate<T> powerFilter) {
		return (float) modify(entity, powerClass, (double) baseValue, powerFilter, null);
	}

	public static <T extends ModifierPower> float modify(Entity entity, Class<T> powerClass, float baseValue, Predicate<T> powerFilter, Consumer<T> powerAction) {
		return (float) modify(entity, powerClass, (double) baseValue, powerFilter, powerAction);
	}

	public static <T extends ModifierPower> double modify(Entity entity, Class<T> powerClass, double baseValue) {
		return modify(entity, powerClass, baseValue, null, null);
	}

	public static <T extends ModifierPower> double modify(Entity entity, Class<T> powerClass, double baseValue, Predicate<T> powerFilter, Consumer<T> powerAction) {
		if (((CraftEntity) entity).getHandle() instanceof LivingEntity living) {
			List<T> powers = getPowers(entity, powerClass);
			List<Modifier> mps = powers.stream()
				.filter(p -> powerFilter == null || powerFilter.test(p))
				.flatMap(p -> p.getModifiers().stream()).collect(Collectors.toList());
			if (powerAction != null) {
				powers.stream().filter(p -> powerFilter == null || powerFilter.test(p)).forEach(powerAction);
			}

			getPowers(entity, AttributeModifyTransferPower.class).stream()
				.filter(p -> p.doesApply(powerClass)).forEach(p -> p.addModifiers(mps, living));
			return ModifierUtil.applyModifiers(living, mps, baseValue);
		}
		return baseValue;
	}

}
