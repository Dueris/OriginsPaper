package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class OffsetCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("offset"),
			SerializableData.serializableData()
				.add("condition", ApoliDataTypes.BLOCK_CONDITION)
				.add("x", SerializableDataTypes.INT, 0)
				.add("y", SerializableDataTypes.INT, 0)
				.add("z", SerializableDataTypes.INT, 0),
			(data, block) -> ((ConditionFactory<BlockInWorld>) data.get("condition"))
				.test(new BlockInWorld(
					block.getLevel(),
					block.getPos().offset(
						data.getInt("x"),
						data.getInt("y"),
						data.getInt("z")
					), true))
		);
	}
}
