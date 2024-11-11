package io.github.dueris.originspaper.action.type.item.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.action.context.ItemActionContext;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.action.type.meta.IfElseListMetaActionType;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.condition.context.ItemConditionContext;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IfElseListItemActionType extends ItemActionType implements IfElseListMetaActionType<ItemActionContext, ItemConditionContext, ItemAction, ItemCondition> {

	private final List<ConditionedAction<ItemAction, ItemCondition>> conditionedActions;

	public IfElseListItemActionType(List<ConditionedAction<ItemAction, ItemCondition>> conditionedActions) {
		this.conditionedActions = conditionedActions;
	}

	@Override
	protected void execute(Level world, SlotAccess stackReference) {
		executeActions(new ItemActionContext(world, stackReference));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return ItemActionTypes.IF_ELSE_LIST;
	}

	@Override
	public List<ConditionedAction<ItemAction, ItemCondition>> conditionedActions() {
		return conditionedActions;
	}

}
