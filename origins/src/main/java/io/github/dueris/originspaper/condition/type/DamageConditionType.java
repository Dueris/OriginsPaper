package io.github.dueris.originspaper.condition.type;

import io.github.dueris.originspaper.condition.DamageCondition;
import io.github.dueris.originspaper.condition.context.DamageConditionContext;
import net.minecraft.world.damagesource.DamageSource;

public abstract class DamageConditionType extends AbstractConditionType<DamageConditionContext, DamageCondition> {

	@Override
	public boolean test(DamageConditionContext context) {
		return test(context.source(), context.amount());
	}

	@Override
	public DamageCondition createCondition(boolean inverted) {
		return new DamageCondition(this, inverted);
	}

	public abstract boolean test(DamageSource source, float amount);

}
