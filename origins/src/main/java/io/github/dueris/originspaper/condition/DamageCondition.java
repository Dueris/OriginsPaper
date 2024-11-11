package io.github.dueris.originspaper.condition;

import io.github.dueris.originspaper.condition.context.DamageConditionContext;
import io.github.dueris.originspaper.condition.type.DamageConditionType;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.calio.data.SerializableDataType;
import net.minecraft.world.damagesource.DamageSource;

public final class DamageCondition extends AbstractCondition<DamageConditionContext, DamageConditionType> {

	public static final SerializableDataType<DamageCondition> DATA_TYPE = SerializableDataType.lazy(() -> ApoliDataTypes.condition("type", DamageConditionTypes.DATA_TYPE, DamageCondition::new));

	public DamageCondition(DamageConditionType conditionType, boolean inverted) {
		super(conditionType, inverted);
	}

	public DamageCondition(DamageConditionType conditionType) {
		this(conditionType, false);
	}

	public boolean test(DamageSource source, float amount) {
		return test(new DamageConditionContext(source, amount));
	}

}
