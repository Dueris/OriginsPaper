package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

public class BoneMealActionType {

	public static void action(Level world, BlockPos pos, Direction direction, boolean showEffects) {

		if (BoneMealItem.growCrop(ItemStack.EMPTY, world, pos)) {

			if (showEffects && !world.isClientSide) {
				world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 0);
			}

		} else {

			BlockState blockState = world.getBlockState(pos);
			BlockPos offsetPos = pos.relative(direction);

			boolean solidSide = blockState.isFaceSturdy(world, pos, direction);

			if (solidSide && BoneMealItem.growWaterPlant(ItemStack.EMPTY, world, offsetPos, direction)) {

				if (showEffects && !world.isClientSide) {
					world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, offsetPos, 0);
				}

			}

		}

	}

	public static @NotNull ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("bonemeal"),
			new SerializableData()
				.add("effects", SerializableDataTypes.BOOLEAN, true),
			(data, block) -> action(block.getLeft(), block.getMiddle(), block.getRight(),
				data.get("effects")
			)
		);
	}

}
