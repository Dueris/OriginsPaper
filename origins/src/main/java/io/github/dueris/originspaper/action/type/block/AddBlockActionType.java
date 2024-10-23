package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;

public class AddBlockActionType {

	public static void action(Level world, BlockPos pos, Direction direction, BlockState blockState) {

		if (direction != null) {
			world.setBlockAndUpdate(pos.relative(direction), blockState);
		}

	}

	public static ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("add_block"),
			new SerializableData()
				.add("block", SerializableDataTypes.BLOCK_STATE),
			(data, block) -> action(block.getLeft(), block.getMiddle(), block.getRight(),
				data.get("block")
			)
		);
	}

}
