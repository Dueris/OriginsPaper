package io.github.dueris.originspaper.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public final class SavedBlockPosition extends BlockInWorld {

	private final BlockState blockState;
	private final BlockEntity blockEntity;

	public SavedBlockPosition(LevelReader world, BlockPos pos, Function<BlockPos, BlockState> blockStateGetter, Function<BlockPos, BlockEntity> blockEntityGetter) {
		super(world, pos, false);
		this.blockState = blockStateGetter.apply(pos);
		this.blockEntity = blockEntityGetter.apply(pos);
	}

	public SavedBlockPosition(LevelReader world, BlockPos pos, BlockState blockState, @Nullable BlockEntity blockEntity) {
		this(world, pos, _pos -> blockState, _pos -> blockEntity);
	}

	public SavedBlockPosition(LevelReader world, BlockPos pos) {
		this(world, pos, _pos -> world.hasChunkAt(_pos) ? world.getBlockState(pos) : null, world::getBlockEntity);
	}

	public static SavedBlockPosition fromLootContext(LootContext context) {

		Vec3 origin = Optional
			.ofNullable(context.getParamOrNull(LootContextParams.ORIGIN))
			.orElse(Vec3.ZERO);

		return new SavedBlockPosition(
			context.getLevel(),
			BlockPos.containing(origin),
			context.getParamOrNull(LootContextParams.BLOCK_STATE),
			context.getParamOrNull(LootContextParams.BLOCK_ENTITY)
		);

	}

	@Override
	public BlockState getState() {
		return blockState;
	}

	@Override
	public BlockEntity getEntity() {
		return blockEntity;
	}

}
