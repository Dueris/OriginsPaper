package io.github.dueris.originspaper.condition.type.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class MovementBlockingConditionType {

	public static boolean condition(BlockInWorld cachedBlock) {
		BlockState state = cachedBlock.getState();
		return state.blocksMotion()
			&& !state.getCollisionShape(cachedBlock.getLevel(), cachedBlock.getPos()).isEmpty();
	}

}
