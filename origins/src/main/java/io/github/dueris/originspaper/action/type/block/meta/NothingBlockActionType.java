package io.github.dueris.originspaper.action.type.block.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.action.type.meta.NothingMetaActionType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class NothingBlockActionType extends BlockActionType implements NothingMetaActionType {

	@Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BlockActionTypes.NOTHING;
	}

}
