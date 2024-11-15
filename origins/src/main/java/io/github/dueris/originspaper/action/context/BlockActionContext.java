package io.github.dueris.originspaper.action.context;

import io.github.dueris.originspaper.condition.context.BlockConditionContext;
import io.github.dueris.originspaper.util.context.TypeActionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record BlockActionContext(Level world, BlockPos pos,
								 Optional<Direction> direction) implements TypeActionContext<BlockConditionContext> {

	@Override
	public BlockConditionContext forCondition() {
		return new BlockConditionContext(world(), pos());
	}

}
