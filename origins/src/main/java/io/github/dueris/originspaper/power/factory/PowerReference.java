package io.github.dueris.originspaper.power.factory;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.registry.ApoliRegistries;
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
		return ApoliRegistries.POWER.getOptional(getId());
	}
}
