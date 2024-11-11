package io.github.dueris.originspaper.condition;

import io.github.dueris.originspaper.condition.context.FluidConditionContext;
import io.github.dueris.originspaper.condition.type.FluidConditionType;
import io.github.dueris.originspaper.condition.type.FluidConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.calio.data.SerializableDataType;
import net.minecraft.world.level.material.FluidState;

public final class FluidCondition extends AbstractCondition<FluidConditionContext, FluidConditionType> {

	public static final SerializableDataType<FluidCondition> DATA_TYPE = SerializableDataType.lazy(() -> ApoliDataTypes.condition("type", FluidConditionTypes.DATA_TYPE, FluidCondition::new));

	public FluidCondition(FluidConditionType conditionType, boolean inverted) {
		super(conditionType, inverted);
	}

	public FluidCondition(FluidConditionType conditionType) {
		this(conditionType, false);
	}

	public boolean test(FluidState fluidState) {
		return test(new FluidConditionContext(fluidState));
	}

}
