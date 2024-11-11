package io.github.dueris.originspaper.action.type.bientity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.context.BiEntityActionContext;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.IfElseListMetaActionType;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.context.BiEntityConditionContext;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IfElseListBiEntityActionType extends BiEntityActionType implements IfElseListMetaActionType<BiEntityActionContext, BiEntityConditionContext, BiEntityAction, BiEntityCondition> {

	private final List<IfElseListMetaActionType.ConditionedAction<BiEntityAction, BiEntityCondition>> conditionedActions;

	public IfElseListBiEntityActionType(List<ConditionedAction<BiEntityAction, BiEntityCondition>> conditionedActions) {
		this.conditionedActions = conditionedActions;
	}

	@Override
	protected void execute(Entity actor, Entity target) {
		executeActions(new BiEntityActionContext(actor, target));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BiEntityActionTypes.IF_ELSE_LIST;
	}

	@Override
	public List<ConditionedAction<BiEntityAction, BiEntityCondition>> conditionedActions() {
		return conditionedActions;
	}

}
