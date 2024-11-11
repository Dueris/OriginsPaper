package io.github.dueris.originspaper.condition.type.damage.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.DamageCondition;
import io.github.dueris.originspaper.condition.context.DamageConditionContext;
import io.github.dueris.originspaper.condition.type.DamageConditionType;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.AnyOfMetaConditionType;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnyOfDamageConditionType extends DamageConditionType implements AnyOfMetaConditionType<DamageConditionContext, DamageCondition> {

	private final List<DamageCondition> conditions;

	public AnyOfDamageConditionType(List<DamageCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public boolean test(DamageSource source, float amount) {
		return testConditions(new DamageConditionContext(source, amount));
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return DamageConditionTypes.ANY_OF;
	}

	@Override
	public List<DamageCondition> conditions() {
		return conditions;
	}

}
