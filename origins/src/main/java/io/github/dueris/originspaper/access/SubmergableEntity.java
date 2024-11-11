package io.github.dueris.originspaper.access;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public interface SubmergableEntity {

	boolean apoli$isSubmergedInLoosely(TagKey<Fluid> fluidTag);

	double apoli$getFluidHeightLoosely(TagKey<Fluid> fluidTag);
}
