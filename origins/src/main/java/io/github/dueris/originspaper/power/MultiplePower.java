package io.github.dueris.originspaper.power;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiplePower extends Power {

	private ImmutableSet<ResourceLocation> subPowerIds;

	MultiplePower(Power basePower, Set<ResourceLocation> subPowerIds) {
		super(basePower);
		this.subPowerIds = ImmutableSet.copyOf(subPowerIds);
	}

	MultiplePower(Power basePower) {
		super(basePower);
		this.subPowerIds = ImmutableSet.of();
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
