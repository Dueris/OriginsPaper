package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class InTagConditionType {

	public static boolean condition(@NotNull BlockInWorld cachedBlock, TagKey<Block> blockTag) {
		return cachedBlock.getState().is(blockTag);
	}

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("in_tag"),
			new SerializableData()
				.add("tag", SerializableDataTypes.BLOCK_TAG),
			(data, cachedBlock) -> condition(cachedBlock,
				data.get("tag")
			)
		);
	}

}
