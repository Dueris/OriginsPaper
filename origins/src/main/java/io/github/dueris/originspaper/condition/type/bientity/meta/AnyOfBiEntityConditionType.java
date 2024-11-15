package io.github.dueris.originspaper.condition.type.bientity.meta;

import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.context.BiEntityConditionContext;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.AnyOfMetaConditionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnyOfBiEntityConditionType extends BiEntityConditionType implements AnyOfMetaConditionType<BiEntityConditionContext, BiEntityCondition> {

	private final List<BiEntityCondition> conditions;

	public AnyOfBiEntityConditionType(List<BiEntityCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiEntityConditionTypes.ANY_OF;
	}

	@Override
	public boolean test(Entity actor, Entity target) {
		return testConditions(new BiEntityConditionContext(actor, target));
	}

	@Override
	public List<BiEntityCondition> conditions() {
		return conditions;
	}

}
