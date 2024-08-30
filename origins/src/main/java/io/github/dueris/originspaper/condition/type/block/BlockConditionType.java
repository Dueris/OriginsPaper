package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class BlockConditionType {

	public static boolean condition(@NotNull BlockInWorld cachedBlock, Block block) {
		return cachedBlock.getState().is(block);
	}

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("block"),
			new SerializableData()
				.add("block", SerializableDataTypes.BLOCK),
			(data, cachedBlock) -> condition(cachedBlock,
				data.get("block")
			)
		);
	}

}
