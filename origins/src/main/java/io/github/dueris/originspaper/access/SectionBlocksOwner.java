package io.github.dueris.originspaper.access;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;

public interface SectionBlocksOwner {
	void apoli$setBlockStates(BlockState[] states);
	BlockState[] apoli$getBlockStates();

	SectionPos apoli$sectionPos();
	short[] apoli$positions();
}
