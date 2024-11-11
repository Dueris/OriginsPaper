package io.github.dueris.originspaper.condition.type.entity.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.context.EntityConditionContext;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.AnyOfMetaConditionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnyOfEntityConditionType extends EntityConditionType implements AnyOfMetaConditionType<EntityConditionContext, EntityCondition> {

	private final List<EntityCondition> conditions;

	public AnyOfEntityConditionType(List<EntityCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public boolean test(Entity entity) {
		return testConditions(new EntityConditionContext(entity));
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.ANY_OF;
	}

	@Override
	public List<EntityCondition> conditions() {
		return conditions;
	}

}
