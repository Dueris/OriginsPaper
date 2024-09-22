package io.github.dueris.originspaper.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class SavedBlockPosition extends BlockInWorld {

	private final BlockState blockState;
	private final BlockEntity blockEntity;

	public SavedBlockPosition(LevelReader world, BlockPos pos) {
		this(world, pos, true);
	}

	public SavedBlockPosition(LevelReader world, BlockPos pos, boolean forceload) {
		this(world, pos, world::getBlockState, world::getBlockEntity, forceload);
	}

	public SavedBlockPosition(LevelReader world, BlockPos pos, Function<BlockPos, BlockState> blockStateFunction, Function<BlockPos, BlockEntity> blockEntityFunction, boolean forceload) {
		super(world, pos, forceload);
		this.blockState = blockStateFunction.apply(pos);
		this.blockEntity = blockEntityFunction.apply(pos);
	}

	public static SavedBlockPosition fromLootContext(LootContext context) {

		Vec3 origin = context.hasParam(LootContextParams.ORIGIN)
			? context.getParam(LootContextParams.ORIGIN)
			: Vec3.ZERO;

		return new SavedBlockPosition(
			context.getLevel(),
			BlockPos.containing(origin),
			pos -> context.getParamOrNull(LootContextParams.BLOCK_STATE),
			pos -> context.getParamOrNull(LootContextParams.BLOCK_ENTITY),
			false
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
