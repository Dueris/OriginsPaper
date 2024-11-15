package io.github.dueris.originspaper.condition.context;

import io.github.dueris.originspaper.util.SavedBlockPosition;
import io.github.dueris.originspaper.util.context.TypeConditionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record BlockConditionContext(SavedBlockPosition savedBlockPosition) implements TypeConditionContext {

	public BlockConditionContext(Level world, BlockPos pos, BlockState blockState, Optional<BlockEntity> blockEntity) {
		this(world, pos, blockState, blockEntity.orElse(null));
	}

	public BlockConditionContext(Level world, BlockPos pos, BlockState blockState, @Nullable BlockEntity blockEntity) {
		this(new SavedBlockPosition(world, pos, blockState, blockEntity));
	}

	public BlockConditionContext(Level world, BlockPos pos) {
		this(world, pos, world.getBlockState(pos), Optional.ofNullable(world.getChunkAt(pos).blockEntities.get(pos)));
	}

	public Level world() {
		return (Level) savedBlockPosition().getLevel();
	}

	public BlockPos pos() {
		return savedBlockPosition().getPos();
	}

	public BlockState blockState() {
		return savedBlockPosition().getState();
	}

	public Optional<BlockEntity> blockEntity() {
		return Optional.ofNullable(savedBlockPosition().getEntity());
	}

}
