package io.github.dueris.originspaper.condition.type;

import io.github.dueris.originspaper.condition.FluidCondition;
import io.github.dueris.originspaper.condition.context.FluidConditionContext;
import net.minecraft.world.level.material.FluidState;

public abstract class FluidConditionType extends AbstractConditionType<FluidConditionContext, FluidCondition> {

	@Override
	public boolean test(FluidConditionContext context) {
		return test(context.fluidState());
	}

	@Override
	public FluidCondition createCondition(boolean inverted) {
		return new FluidCondition(this, inverted);
	}

	public abstract boolean test(FluidState fluidState);

}
