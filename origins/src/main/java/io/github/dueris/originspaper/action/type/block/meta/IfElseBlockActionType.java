package io.github.dueris.originspaper.action.type.block.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.context.BlockActionContext;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.action.type.meta.IfElseMetaActionType;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.context.BlockConditionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class IfElseBlockActionType extends BlockActionType implements IfElseMetaActionType<BlockActionContext, BlockConditionContext, BlockAction, BlockCondition> {

	private final BlockCondition condition;

	private final BlockAction ifAction;
	private final Optional<BlockAction> elseAction;

	public IfElseBlockActionType(BlockCondition condition, BlockAction ifAction, Optional<BlockAction> elseAction) {
		this.condition = condition;
		this.ifAction = ifAction;
		this.elseAction = elseAction;
	}

	@Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {
		executeAction(new BlockActionContext(world, pos, direction));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BlockActionTypes.IF_ELSE;
	}

	@Override
	public BlockCondition condition() {
		return condition;
	}

	@Override
	public BlockAction ifAction() {
		return ifAction;
	}

	@Override
	public Optional<BlockAction> elseAction() {
		return elseAction;
	}

}
