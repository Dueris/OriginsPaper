package io.github.dueris.originspaper.power.factory;

import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.resources.ResourceLocation;

public record PowerReference(ResourceLocation location) {

	public ResourceLocation getId() {
		return location;
	}

	public PowerType getType() {
		return OriginsPaper.getPower(getId());
	}
}
