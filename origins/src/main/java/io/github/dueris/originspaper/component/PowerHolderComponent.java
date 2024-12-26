package io.github.dueris.originspaper.component;

import com.google.common.collect.Lists;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.power.type.AttributeModifyTransferPowerType;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.power.type.ValueModifyingPowerType;
import io.github.dueris.originspaper.util.ProvidableComponentKey;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface PowerHolderComponent {

	ProvidableComponentKey<PowerHolderComponent, Entity> KEY = new ProvidableComponentKey<>(e -> e instanceof LivingEntity living ? new PowerHolderComponentImpl(living) : null);

	/**
	 * Queries the {@link PowerHolderComponent} from an {@link Entity}. This is safer and preferred than directly using
	 * {@link #KEY} as it handles certain scenarios where unexpected errors may occur.
	 *
	 * @param entity the entity to get the component from
	 * @return the power component, or {@link Optional#empty()} if the entity is either null, its component
	 * container hasn't been initialized yet, or if the entity doesn't/can't have the power component
	 */
	static Optional<PowerHolderComponent> getOptional(@Nullable Entity entity) {

		if (entity != null) {
			return KEY.maybeGet(entity);
		} else {
			return Optional.empty();
		}

	}

	/**
	 * An alternative version of {@link #getOptional(Entity)} that returns a <b>nullable</b>
	 * {@link PowerHolderComponent} instead.
	 *
	 * @param entity the entity to get the power component from
	 * @return the power component, or {@code null} if the entity is either null, its component container
	 * hasn't been initialized yet, or if the entity doesn't/can't have the power component
	 */
	@Nullable
	static PowerHolderComponent getNullable(@Nullable Entity entity) {
		return getOptional(entity).orElse(null);
	}

	static void sync(Entity entity) {
		getOptional(entity).ifPresent(PowerHolderComponent::sync);
	}

	static boolean grantPower(@NotNull Entity entity, Power power, ResourceLocation source, boolean sync) {
		return grantPowers(entity, Map.of(source, List.of(power)), sync);
	}

	static boolean grantPowers(@NotNull Entity entity, Map<ResourceLocation, Collection<Power>> powersBySource, boolean sync) {

		PowerHolderComponent powerComponent = getNullable(entity);
		if (!entity.level().isClientSide() && powerComponent != null) {

			boolean granted = powersBySource.entrySet()
				.stream()
				.flatMap(e -> e.getValue()
					.stream()
					.map(power -> powerComponent.addPower(power, e.getKey())))
				.reduce(false, Boolean::logicalOr);

			if (granted && sync) {
				// PacketHandlers.GRANT_POWERS.sync(entity, powersBySource); // OriginsPaper - nope! SSO :)
			}

			return granted;

		} else {
			return false;
		}

	}

	static boolean revokePower(@NotNull Entity entity, Power power, ResourceLocation source, boolean sync) {
		return revokePowers(entity, Map.of(source, List.of(power)), sync);
	}

	static boolean revokePowers(@NotNull Entity entity, Map<ResourceLocation, Collection<Power>> powersBySource, boolean sync) {

		PowerHolderComponent powerComponent = getNullable(entity);
		if (!entity.level().isClientSide() && powerComponent != null) {

			boolean revoked = powersBySource.entrySet()
				.stream()
				.flatMap(e -> e.getValue()
					.stream()
					.map(power -> powerComponent.removePower(power, e.getKey())))
				.reduce(false, Boolean::logicalOr);

			if (revoked && sync) {
				// PacketHandlers.REVOKE_POWERS.sync(entity, powersBySource); // OriginsPaper - nope! SSO :)
			}

			return revoked;

		} else {
			return false;
		}

	}

	static int revokeAllPowersFromSource(@NotNull Entity entity, ResourceLocation source, boolean sync) {
		return revokeAllPowersFromAllSources(entity, List.of(source), sync);
	}

	static int revokeAllPowersFromAllSources(@NotNull Entity entity, Collection<ResourceLocation> sources, boolean sync) {

		PowerHolderComponent powerComponent = getNullable(entity);
		if (!entity.level().isClientSide() && powerComponent != null) {

			int revokedPowers = sources
				.stream()
				.map(powerComponent::removeAllPowersFromSource)
				.reduce(0, Integer::sum);

			if (revokedPowers > 0 && sync) {
				// PacketHandlers.REVOKE_ALL_POWERS.sync(entity, sources); // OriginsPaper - nope! SSO :)
			}

			return revokedPowers;

		} else {
			return 0;
		}

	}

	static void syncPower(Entity entity, @NotNull PowerReference powerReference) {
		syncPower(entity, powerReference.getPower());
	}

	static void syncPower(@Nullable Entity entity, @Nullable Power power) {

		if (power == null || entity == null || entity.level().isClientSide()) {
			return;
		}

		PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(entity);
		if (component == null) {
			return;
		}

		CompoundTag powerData = new CompoundTag();
		PowerType powerType = component.getPowerType(power);

		if (powerType == null) {
			return;
		}

		powerData.put("Data", powerType.toTag());
	}

	static <P extends Power> void syncPowers(@Nullable Entity entity, Collection<P> powers) {

		if (entity == null || entity.level().isClientSide() || powers.isEmpty()) {
			return;
		}

		PowerHolderComponent component = getNullable(entity);
		Map<ResourceLocation, Tag> powersToSync = new HashMap<>();

		if (component == null) {
			return;
		}

		for (Power power : powers) {

			PowerType powerType = component.getPowerType(power);

			if (powerType != null) {
				powersToSync.put(power.getId(), powerType.toTag());
			}

		}

		if (powersToSync.isEmpty()) {
		}

	}

	static <T extends PowerType> boolean withPowerType(@Nullable Entity entity, Class<T> powerClass, @NotNull Predicate<T> filter, Consumer<T> action) {

		Optional<T> powerType = getOptional(entity)
			.stream()
			.map(powerComponent -> powerComponent.getPowerTypes(powerClass))
			.flatMap(Collection::stream)
			.filter(filter)
			.findFirst();

		powerType.ifPresent(action);
		return powerType.isPresent();

	}

	static <T extends PowerType> boolean withPowerTypes(@Nullable Entity entity, Class<T> powerClass, @NotNull Predicate<T> filter, @NotNull Consumer<T> action) {

		List<T> powerTypes = getOptional(entity)
			.stream()
			.map(pc -> pc.getPowerTypes(powerClass))
			.flatMap(Collection::stream)
			.filter(filter)
			.toList();

		powerTypes.forEach(action);
		return !powerTypes.isEmpty();

	}

	static <T extends PowerType> List<T> getPowerTypes(Entity entity, Class<T> powerClass) {
		return getPowerTypes(entity, powerClass, false);
	}

	static <T extends PowerType> List<T> getPowerTypes(Entity entity, Class<T> powerClass, boolean includeInactive) {
		return getOptional(entity)
			.map(powerComponent -> powerComponent.getPowerTypes(powerClass, includeInactive))
			.orElse(Lists.newArrayList());
	}

	static <T extends PowerType> boolean hasPowerType(Entity entity, Class<T> powerClass) {
		return hasPowerType(entity, powerClass, p -> true);
	}

	static <T extends PowerType> boolean hasPowerType(Entity entity, Class<T> typeClass, @NotNull Predicate<T> typeFilter) {
		return getOptional(entity)
			.stream()
			.map(PowerHolderComponent::getPowerTypes)
			.flatMap(Collection::stream)
			.filter(typeClass::isInstance)
			.map(typeClass::cast)
			.filter(PowerType::isActive)
			.anyMatch(typeFilter);
	}

	static <T extends ValueModifyingPowerType> float modify(Entity entity, Class<T> powerClass, float baseValue) {
		return (float) modify(entity, powerClass, (double) baseValue, p -> true, p -> {
		});
	}

	static <T extends ValueModifyingPowerType> float modify(Entity entity, Class<T> powerClass, float baseValue, Predicate<T> powerFilter) {
		return (float) modify(entity, powerClass, (double) baseValue, powerFilter, p -> {
		});
	}

	static <T extends ValueModifyingPowerType> float modify(Entity entity, Class<T> powerClass, float baseValue, Predicate<T> powerFilter, Consumer<T> powerAction) {
		return (float) modify(entity, powerClass, (double) baseValue, powerFilter, powerAction);
	}

	static <T extends ValueModifyingPowerType> double modify(Entity entity, Class<T> powerClass, double baseValue) {
		return modify(entity, powerClass, baseValue, p -> true, p -> {
		});
	}

	static <T extends ValueModifyingPowerType> double modify(Entity entity, Class<T> powerClass, double baseValue, @NotNull Predicate<T> powerFilter, @NotNull Consumer<T> powerAction) {

		PowerHolderComponent powerComponent = getNullable(entity);
		if (powerComponent != null) {

			List<Modifier> modifiers = powerComponent.getPowerTypes(powerClass)
				.stream()
				.filter(powerFilter)
				.peek(powerAction)
				.flatMap(p -> p.getModifiers().stream())
				.collect(Collectors.toCollection(ArrayList::new));

			powerComponent.getPowerTypes(AttributeModifyTransferPowerType.class)
				.stream()
				.filter(p -> p.doesApply(powerClass))
				.forEach(p -> p.addModifiers(modifiers));

			return ModifierUtil.applyModifiers(entity, modifiers, baseValue);

		} else {
			return baseValue;
		}

	}

	boolean removePower(Power power, ResourceLocation source);

	default boolean removePower(PowerReference powerReference, ResourceLocation source) {
		return powerReference.getResultPower()
			.mapError(err -> "Couldn't revoke non-existing power with ID \"" + powerReference.id() + "\"!")
			.resultOrPartial(OriginsPaper.LOGGER::warn)
			.map(power -> removePower(power, source))
			.orElse(false);
	}

	int removeAllPowersFromSource(ResourceLocation source);

	List<Power> getPowersFromSource(ResourceLocation source);

	boolean addPower(Power power, ResourceLocation source);

	default boolean addPower(PowerReference powerReference, ResourceLocation source) {
		return powerReference.getResultPower()
			.mapError(error -> "Couldn't grant non-existing power with ID \"" + powerReference.id() + "\"!")
			.resultOrPartial(OriginsPaper.LOGGER::warn)
			.map(power -> addPower(power, source))
			.orElse(false);
	}

	boolean hasPower(Power power);

	default boolean hasPower(PowerReference powerReference) {
		return powerReference.getOptionalPower()
			.map(this::hasPower)
			.orElse(false);
	}

	boolean hasPower(Power power, ResourceLocation source);

	default boolean hasPower(PowerReference powerReference, ResourceLocation source) {
		return powerReference.getOptionalPower()
			.map(power -> hasPower(power, source))
			.orElse(false);
	}

	PowerType getPowerType(Power power);

	List<PowerType> getPowerTypes();

	Set<Power> getPowers(boolean includeSubPowers);

	<T extends PowerType> List<T> getPowerTypes(Class<T> typeClass);

	<T extends PowerType> List<T> getPowerTypes(Class<T> typeClass, boolean includeInactive);

	List<ResourceLocation> getSources(Power power);

	default List<ResourceLocation> getSources(PowerReference powerReference) {
		return powerReference.getOptionalPower()
			.map(this::getSources)
			.orElseGet(ArrayList::new);
	}

	void sync();

	void serverTick();

	void readFromNbt(CompoundTag tag, HolderLookup.Provider lookup);

	void writeToNbt(CompoundTag compoundTag, HolderLookup.Provider lookup);

}
