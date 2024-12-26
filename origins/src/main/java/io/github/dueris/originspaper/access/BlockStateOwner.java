package io.github.dueris.originspaper.access;

import net.minecraft.world.level.block.state.BlockState;

public interface BlockStateOwner {
	void apoli$setBlockState(BlockState state);

	BlockState apoli$getBlockState();
}
