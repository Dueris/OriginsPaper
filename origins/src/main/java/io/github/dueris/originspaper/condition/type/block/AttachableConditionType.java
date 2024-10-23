package io.github.dueris.originspaper.condition.type.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class AttachableConditionType {

	public static boolean condition(BlockInWorld cachedBlock) {

		LevelReader worldView = cachedBlock.getLevel();
		BlockPos originPos = cachedBlock.getPos();

		for (Direction direction : Direction.values()) {

			BlockPos offsetPos = originPos.relative(direction);
			BlockState adjacentState = worldView.getBlockState(offsetPos);

			if (adjacentState.isFaceSturdy(worldView, originPos, direction.getOpposite())) {
				return true;
			}

		}

		return false;

	}

}
