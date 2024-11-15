package io.github.dueris.originspaper.condition;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.condition.context.BiEntityConditionContext;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.entity.Entity;

public final class BiEntityCondition extends AbstractCondition<BiEntityConditionContext, BiEntityConditionType> {

	public static final SerializableDataType<BiEntityCondition> DATA_TYPE = SerializableDataType.lazy(() -> ApoliDataTypes.condition("type", BiEntityConditionTypes.DATA_TYPE, BiEntityCondition::new));

	public BiEntityCondition(BiEntityConditionType conditionType, boolean inverted) {
		super(conditionType, inverted);
	}

	public BiEntityCondition(BiEntityConditionType conditionType) {
		this(conditionType, false);
	}

	public boolean test(Entity actor, Entity target) {
		return test(new BiEntityConditionContext(actor, target));
	}

}
