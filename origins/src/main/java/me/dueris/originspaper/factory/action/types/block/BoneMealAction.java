package me.dueris.originspaper.factory.action.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

public class BoneMealAction {

	public static void action(@NotNull DeserializedFactoryJson data, @NotNull Triple<Level, BlockPos, Direction> block) {
		Level world = block.getLeft();
		BlockPos blockPos = block.getMiddle();
		Direction side = block.getRight();
		BlockPos blockPos2 = blockPos.relative(side);

		boolean spawnEffects = data.getBoolean("effects");

		if (BoneMealItem.growCrop(ItemStack.EMPTY, world, blockPos)) {
			if (spawnEffects && !world.isClientSide) {
				world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos, 0);
			}
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			boolean bl = blockState.isFaceSturdy(world, blockPos, side);
			if (bl && BoneMealItem.growWaterPlant(ItemStack.EMPTY, world, blockPos2, side)) {
				if (spawnEffects && !world.isClientSide) {
					world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos2, 0);
				}
			}
		}
	}

	public static @NotNull ActionFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("bonemeal"),
			InstanceDefiner.instanceDefiner()
				.add("effects", SerializableDataTypes.BOOLEAN, true),
			BoneMealAction::action
		);
	}
}
