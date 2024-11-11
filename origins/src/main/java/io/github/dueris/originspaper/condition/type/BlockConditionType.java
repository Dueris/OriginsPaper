package io.github.dueris.originspaper.condition.type;

import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.context.BlockConditionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public abstract class BlockConditionType extends AbstractConditionType<BlockConditionContext, BlockCondition> {

	@Override
	public BlockCondition createCondition(boolean inverted) {
		return new BlockCondition(this, inverted);
	}

	@Override
	public boolean test(BlockConditionContext context) {
		return context.blockState() != null
			&& test(context.world(), context.pos(), context.blockState(), context.blockEntity());
	}

	public abstract boolean test(Level world, BlockPos pos, BlockState blockState, Optional<BlockEntity> blockEntity);

}
