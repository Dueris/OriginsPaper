package io.github.dueris.originspaper.action.type.entity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.context.EntityActionContext;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.IfElseListMetaActionType;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.context.EntityConditionContext;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IfElseListEntityActionType extends EntityActionType implements IfElseListMetaActionType<EntityActionContext, EntityConditionContext, EntityAction, EntityCondition> {

	private final List<ConditionedAction<EntityAction, EntityCondition>> conditionedActions;

	public IfElseListEntityActionType(List<ConditionedAction<EntityAction, EntityCondition>> conditionedActions) {
		this.conditionedActions = conditionedActions;
	}

	@Override
	protected void execute(Entity entity) {
		executeActions(new EntityActionContext(entity));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.IF_ELSE_LIST;
	}

	@Override
	public List<ConditionedAction<EntityAction, EntityCondition>> conditionedActions() {
		return conditionedActions;
	}

}
