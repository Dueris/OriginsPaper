package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BoneMealBlockActionType extends BlockActionType {

	public static final TypedDataObjectFactory<BoneMealBlockActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("effects", SerializableDataTypes.BOOLEAN, true),
		data -> new BoneMealBlockActionType(
			data.get("effects")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("effects", actionType.showEffects)
	);

	private final boolean showEffects;

	public BoneMealBlockActionType(boolean showEffects) {
		this.showEffects = showEffects;
	}

	@Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {

		if (BoneMealItem.growCrop(ItemStack.EMPTY, world, pos)) {

			if (showEffects && !world.isClientSide()) {
				world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 0);
			}

		} else if (direction.isPresent()) {

			Direction dir = direction.get();

			BlockState blockState = world.getBlockState(pos);
			BlockPos offsetPos = pos.relative(dir);

			boolean solidSide = blockState.isFaceSturdy(world, pos, dir);
			if (solidSide && BoneMealItem.growWaterPlant(ItemStack.EMPTY, world, offsetPos, dir)) {

				if (showEffects && !world.isClientSide()) {
					world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, offsetPos, 0);
				}

			}

		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BlockActionTypes.BONE_MEAL;
	}

}
