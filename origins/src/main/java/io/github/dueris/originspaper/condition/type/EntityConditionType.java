package io.github.dueris.originspaper.condition.type;

import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.context.EntityConditionContext;
import net.minecraft.world.entity.Entity;

public abstract class EntityConditionType extends AbstractConditionType<EntityConditionContext, EntityCondition> {

	@Override
	public boolean test(EntityConditionContext context) {
		return test(context.entity());
	}

	@Override
	public EntityCondition createCondition(boolean inverted) {
		return new EntityCondition(this, inverted);
	}

	public abstract boolean test(Entity entity);

}
