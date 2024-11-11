package io.github.dueris.originspaper.condition;

import io.github.dueris.originspaper.condition.context.EntityConditionContext;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.calio.data.SerializableDataType;
import net.minecraft.world.entity.Entity;

public final class EntityCondition extends AbstractCondition<EntityConditionContext, EntityConditionType> {

	public static final SerializableDataType<EntityCondition> DATA_TYPE = SerializableDataType.lazy(() -> ApoliDataTypes.condition("type", EntityConditionTypes.DATA_TYPE, EntityCondition::new));

	public EntityCondition(EntityConditionType conditionType, boolean inverted) {
		super(conditionType, inverted);
	}

	public EntityCondition(EntityConditionType conditionType) {
		this(conditionType, false);
	}

	public boolean test(Entity entity) {
		return test(new EntityConditionContext(entity));
	}

}
