package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AddBlockBlockActionType extends BlockActionType {

	public static final TypedDataObjectFactory<AddBlockBlockActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("block", SerializableDataTypes.BLOCK_STATE),
		data -> new AddBlockBlockActionType(
			data.get("block")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("block", actionType.blockState)
	);

	private final BlockState blockState;

	public AddBlockBlockActionType(BlockState blockState) {
		this.blockState = blockState;
	}

	@Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {
		direction.ifPresent(dir -> world.setBlockAndUpdate(pos.relative(dir), blockState));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BlockActionTypes.ADD_BLOCK;
	}

}
