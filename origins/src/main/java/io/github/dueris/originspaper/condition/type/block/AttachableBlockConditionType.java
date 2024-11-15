package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BlockConditionType;
import io.github.dueris.originspaper.condition.type.BlockConditionTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AttachableBlockConditionType extends BlockConditionType {

	@Override
	public boolean test(Level world, BlockPos pos, BlockState blockState, Optional<BlockEntity> blockEntity) {

		for (Direction direction : Direction.values()) {

			BlockPos offsetPos = pos.relative(direction);

			if (world.hasChunkAt(offsetPos) && world.getBlockState(offsetPos).isFaceSturdy(world, pos, direction.getOpposite())) {
				return true;
			}

		}

		return false;

	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BlockConditionTypes.ATTACHABLE;
	}

}
