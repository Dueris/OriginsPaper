package io.github.dueris.originspaper.power.factory;

import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PowerReference(ResourceLocation location) {

	public ResourceLocation getId() {
		return location;
	}

	public PowerType getType() {
		return OriginsPaper.getPower(getId());
	}

	public @NotNull Optional<PowerType> getOptionalReference() {
		if (PowerType.REGISTRY.containsKey(location)) {
			return Optional.of(PowerType.REGISTRY.get(location));
		}
		return Optional.empty();
	}
}
