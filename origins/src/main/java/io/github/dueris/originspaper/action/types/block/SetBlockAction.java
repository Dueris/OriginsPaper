package io.github.dueris.originspaper.action.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

public class SetBlockAction {

	public static @NotNull ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("set_block"),
			SerializableData.serializableData()
				.add("block", SerializableDataTypes.BLOCK_STATE),
			(data, block) -> {
				BlockState actualState = data.get("block");
				block.getLeft().setBlockAndUpdate(block.getMiddle(), actualState);
			}
		);
	}
}
