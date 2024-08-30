package io.github.dueris.originspaper.condition.type.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class MovementBlockingConditionType {

	public static boolean condition(@NotNull BlockInWorld cachedBlock) {
		BlockState state = cachedBlock.getState();
		return state.blocksMotion()
			&& !state.getCollisionShape(cachedBlock.getLevel(), cachedBlock.getPos()).isEmpty();
	}

}
