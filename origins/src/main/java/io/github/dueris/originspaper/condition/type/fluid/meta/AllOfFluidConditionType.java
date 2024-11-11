package io.github.dueris.originspaper.condition.type.fluid.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.FluidCondition;
import io.github.dueris.originspaper.condition.context.FluidConditionContext;
import io.github.dueris.originspaper.condition.type.FluidConditionType;
import io.github.dueris.originspaper.condition.type.FluidConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.AllOfMetaConditionType;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AllOfFluidConditionType extends FluidConditionType implements AllOfMetaConditionType<FluidConditionContext, FluidCondition> {

	private final List<FluidCondition> conditions;

	public AllOfFluidConditionType(List<FluidCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public boolean test(FluidState fluidState) {
		return testConditions(new FluidConditionContext(fluidState));
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return FluidConditionTypes.ALL_OF;
	}

	@Override
	public List<FluidCondition> conditions() {
		return conditions;
	}

}
