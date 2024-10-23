package io.github.dueris.originspaper.power;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MultiplePower extends Power {

	private ImmutableSet<ResourceLocation> subPowerIds = ImmutableSet.of();

	MultiplePower(Power basePower, Set<ResourceLocation> subPowerIds) {
		this(basePower);
		this.subPowerIds = ImmutableSet.copyOf(subPowerIds);
	}

	MultiplePower(@NotNull Power basePower) {
		super(basePower.getFactoryInstance(), basePower.data);
	}

	public ImmutableSet<ResourceLocation> getSubPowerIds() {
		return subPowerIds;
	}

	void setSubPowerIds(Set<ResourceLocation> subPowerIds) {
		this.subPowerIds = ImmutableSet.copyOf(subPowerIds);
	}

	public Set<SubPower> getSubPowers() {
		return this.getSubPowerIds()
			.stream()
			.filter(PowerManager::contains)
			.map(PowerManager::get)
			.filter(SubPower.class::isInstance)
			.map(SubPower.class::cast)
			.collect(Collectors.toCollection(HashSet::new));
	}

}

