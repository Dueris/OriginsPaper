package io.github.dueris.originspaper.component;

import com.google.common.collect.Lists;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface PowerHolderComponent {

	ProvidableComponentKey<PowerHolderComponent> KEY = new ProvidableComponentKey<>();

	static boolean grantPower(@NotNull Entity entity, Power power, ResourceLocation source, boolean sync) {
		return grantPowers(entity, Map.of(source, List.of(power)), sync);
	}

	static boolean grantPowers(@NotNull Entity entity, Map<ResourceLocation, Collection<Power>> powersBySource, boolean sync) {

		if (KEY.isProvidedBy(entity) && !entity.level().isClientSide) {

			PowerHolderComponent component = KEY.get(entity);

			return powersBySource.entrySet()
				.stream()
				.flatMap(e -> e.getValue().stream().map(power -> component.addPower(power, e.getKey())))
				.reduce(false, Boolean::logicalOr);

		} else {
			return false;
		}

	}

	static boolean revokePower(@NotNull Entity entity, Power power, ResourceLocation source, boolean sync) {
		return revokePowers(entity, Map.of(source, List.of(power)), sync);
	}

	static boolean revokePowers(@NotNull Entity entity, Map<ResourceLocation, Collection<Power>> powersBySource, boolean sync) {

		if (KEY.isProvidedBy(entity) && !entity.level().isClientSide) {

			PowerHolderComponent component = KEY.get(entity);

			return powersBySource.entrySet()
				.stream()
				.flatMap(e -> e.getValue().stream().map(power -> component.removePower(power, e.getKey())))
				.reduce(false, Boolean::logicalOr);

		} else {
			return false;
		}

	}

	static int revokeAllPowersFromSource(@NotNull Entity entity, ResourceLocation source, boolean sync) {
		return revokeAllPowersFromAllSources(entity, List.of(source), sync);
	}

	static int revokeAllPowersFromAllSources(@NotNull Entity entity, Collection<ResourceLocation> sources, boolean sync) {

		if (KEY.isProvidedBy(entity) && !entity.level().isClientSide) {

			PowerHolderComponent component = KEY.get(entity);

			return sources
				.stream()
				.map(component::removeAllPowersFromSource)
				.reduce(0, Integer::sum);

		} else {
			return 0;
		}

	}

	static void syncPower(Entity entity, Power power) {

		if (entity == null || entity.level().isClientSide) {
			return;
		}

		if (power instanceof PowerReference powerReference) {
			power = powerReference.getReference();
		}

		if (power == null) {
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

	static void syncPowers(Entity entity, Collection<? extends Power> powers) {

		if (entity == null || entity.level().isClientSide || powers.isEmpty()) {
			return;
		}

		PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(entity);
		Map<ResourceLocation, Tag> powersToSync = new HashMap<>();

		if (component == null) {
			return;
		}

		for (Power power : powers) {

			if (power instanceof PowerReference powerReference) {
				power = powerReference.getReference();
			}

			if (power == null) {
				continue;
			}

			PowerType powerType = component.getPowerType(power);
			if (powerType != null) {
				powersToSync.put(power.getId(), powerType.toTag());
			}

		}

		if (powersToSync.isEmpty()) {
		}

	}

	static <T extends PowerType> boolean withPowerType(@Nullable Entity entity, Class<T> powerClass, @NotNull Predicate<T> filter, Consumer<T> action) {

		Optional<T> power = KEY.maybeGet(entity)
			.stream()
			.map(pc -> pc.getPowerTypes(powerClass))
			.flatMap(Collection::stream)
			.filter(filter)
			.findFirst();

		power.ifPresent(action);
		return power.isPresent();

	}

	static <T extends PowerType> boolean withPowerTypes(@Nullable Entity entity, Class<T> powerClass, @NotNull Predicate<T> filter, @NotNull Consumer<T> action) {

		List<T> powerTypes = KEY.maybeGet(entity)
			.stream()
			.flatMap(pc -> pc.getPowerTypes(powerClass).stream())
			.filter(filter)
			.toList();

		powerTypes.forEach(action);
		return !powerTypes.isEmpty();

	}

	static <T extends PowerType> List<T> getPowerTypes(Entity entity, Class<T> powerClass) {
		return getPowerTypes(entity, powerClass, false);
	}

	static <T extends PowerType> List<T> getPowerTypes(Entity entity, Class<T> powerClass, boolean includeInactive) {
		return KEY.maybeGet(entity)
			.map(pc -> pc.getPowerTypes(powerClass, includeInactive))
			.orElse(Lists.newArrayList());
	}

	static <T extends PowerType> boolean hasPowerType(Entity entity, Class<T> powerClass) {
		return hasPowerType(entity, powerClass, p -> true);
	}

	static <T extends PowerType> boolean hasPowerType(Entity entity, @NotNull Class<T> typeClass, @NotNull Predicate<T> typeFilter) {
		return KEY.maybeGet(entity)
			.stream()
			.map(PowerHolderComponent::getPowerTypes)
			.flatMap(Collection::stream)
			.filter(typeClass::isInstance)
			.map(typeClass::cast)
			.anyMatch(type -> type.isActive() && typeFilter.test(type));
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

		if (entity != null && KEY.isProvidedBy(entity)) {

			PowerHolderComponent component = KEY.get(entity);
			List<Modifier> modifiers = component.getPowerTypes(powerClass)
				.stream()
				.filter(powerFilter)
				.peek(powerAction)
				.flatMap(p -> p.getModifiers().stream())
				.collect(Collectors.toCollection(ArrayList::new));

			component.getPowerTypes(AttributeModifyTransferPowerType.class)
				.stream()
				.filter(p -> p.doesApply(powerClass))
				.forEach(p -> p.addModifiers(modifiers));

			return ModifierUtil.applyModifiers(entity, modifiers, baseValue);

		} else {
			return baseValue;
		}

	}

	boolean removePower(Power power, ResourceLocation source);

	int removeAllPowersFromSource(ResourceLocation source);

	List<Power> getPowersFromSource(ResourceLocation source);

	boolean addPower(Power power, ResourceLocation source);

	boolean hasPower(Power power);

	boolean hasPower(Power power, ResourceLocation source);

	PowerType getPowerType(Power power);

	List<PowerType> getPowerTypes();

	Set<Power> getPowers(boolean includeSubPowers);

	<T extends PowerType> List<T> getPowerTypes(Class<T> typeClass);

	<T extends PowerType> List<T> getPowerTypes(Class<T> typeClass, boolean includeInactive);

	List<ResourceLocation> getSources(Power power);

	void serverTick();

	void readFromNbt(CompoundTag tag, HolderLookup.Provider lookup);

	void writeToNbt(CompoundTag compoundTag, HolderLookup.Provider lookup);

}
