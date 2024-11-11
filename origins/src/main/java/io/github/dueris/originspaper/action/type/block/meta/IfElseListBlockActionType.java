package io.github.dueris.originspaper.action.type.block.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.context.BlockActionContext;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.action.type.meta.IfElseListMetaActionType;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.context.BlockConditionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class IfElseListBlockActionType extends BlockActionType implements IfElseListMetaActionType<BlockActionContext, BlockConditionContext, BlockAction, BlockCondition> {

	private final List<ConditionedAction<BlockAction, BlockCondition>> conditionedActions;

	public IfElseListBlockActionType(List<ConditionedAction<BlockAction, BlockCondition>> conditionedActions) {
		this.conditionedActions = conditionedActions;
	}

	@Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {
		executeActions(new BlockActionContext(world, pos, direction));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BlockActionTypes.IF_ELSE_LIST;
	}

	@Override
	public List<ConditionedAction<BlockAction, BlockCondition>> conditionedActions() {
		return conditionedActions;
	}

}
