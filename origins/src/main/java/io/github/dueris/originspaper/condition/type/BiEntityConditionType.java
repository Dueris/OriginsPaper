package io.github.dueris.originspaper.condition.type;

import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.context.BiEntityConditionContext;
import net.minecraft.world.entity.Entity;

public abstract class BiEntityConditionType extends AbstractConditionType<BiEntityConditionContext, BiEntityCondition> {

	@Override
	public boolean test(BiEntityConditionContext context) {
		return test(context.actor(), context.target());
	}

	@Override
	public BiEntityCondition createCondition(boolean inverted) {
		return new BiEntityCondition(this, inverted);
	}

	public abstract boolean test(Entity actor, Entity target);

}
