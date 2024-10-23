package io.github.dueris.originspaper.global;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.Power;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GlobalPowerSetUtil {

	public static final ResourceLocation POWER_SOURCE = OriginsPaper.apoliIdentifier("global");

	public static List<GlobalPowerSet> getApplicableSets(EntityType<?> type) {
		return GlobalPowerSetManager.values()
			.stream()
			.filter(gps -> gps.doesApply(type))
			.sorted(GlobalPowerSet::compareTo)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Set<ResourceLocation> getPowerIds(List<GlobalPowerSet> powerSets) {
		return powerSets.stream()
			.flatMap(gps -> gps.getPowers().stream())
			.map(Power::getId)
			.collect(Collectors.toSet());
	}

	public static Set<Power> flattenPowers(Collection<GlobalPowerSet> sets) {
		return sets
			.stream()
			.map(GlobalPowerSet::getPowers)
			.flatMap(Collection::stream)
			.collect(Collectors.toSet());
	}

	public static void applyGlobalPowers(Entity entity) {

		if (entity.level().isClientSide || !PowerHolderComponent.KEY.isProvidedBy(entity)) {
			return;
		}

		PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);

		List<GlobalPowerSet> globalPowerSets = getApplicableSets(entity.getType());
		Set<Power> powers = flattenPowers(globalPowerSets);

		Set<Power> removedPowers = removeExcessPowers(component, powers);
		Set<Power> addedPowers = addMissingPowers(component, powers);

		// OriginsPaper - no networking since we server-sided :)
		/* if (!removedPowers.isEmpty()) {
			PowerHolderComponent.PacketHandlers.REVOKE_POWERS.sync(entity, Map.of(POWER_SOURCE, removedPowers));
		}

		if (!addedPowers.isEmpty()) {
			PowerHolderComponent.PacketHandlers.GRANT_POWERS.sync(entity, Map.of(POWER_SOURCE, addedPowers));
		} */

	}

	private static Set<Power> removeExcessPowers(PowerHolderComponent component, Set<Power> expected) {
		return component.getPowersFromSource(POWER_SOURCE)
			.stream()
			.filter(Predicate.not(expected::contains))
			.filter(power -> component.removePower(power, POWER_SOURCE))
			.collect(Collectors.toSet());
	}

	private static Set<Power> addMissingPowers(PowerHolderComponent component, Set<Power> powers) {
		return powers
			.stream()
			.filter(power -> component.addPower(power, POWER_SOURCE))
			.collect(Collectors.toSet());
	}

}
