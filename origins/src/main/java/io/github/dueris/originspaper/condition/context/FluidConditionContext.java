package io.github.dueris.originspaper.condition.context;

import io.github.dueris.originspaper.util.context.TypeConditionContext;
import net.minecraft.world.level.material.FluidState;

public record FluidConditionContext(FluidState fluidState) implements TypeConditionContext {

}
