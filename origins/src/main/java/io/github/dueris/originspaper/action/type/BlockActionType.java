package io.github.dueris.originspaper.action.type;

import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.context.BlockActionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.Optional;

public abstract class BlockActionType extends AbstractActionType<BlockActionContext, BlockAction> {

	@Override
	public void accept(BlockActionContext context) {
		execute(context.world(), context.pos(), context.direction());
	}

	@Override
	public BlockAction createAction() {
		return new BlockAction(this);
	}

	protected abstract void execute(Level world, BlockPos pos, Optional<Direction> direction);

}
