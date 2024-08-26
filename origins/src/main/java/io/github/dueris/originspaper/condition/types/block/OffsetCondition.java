package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class OffsetCondition {

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("offset"),
			SerializableData.serializableData()
				.add("condition", ApoliDataTypes.BLOCK_CONDITION)
				.add("x", SerializableDataTypes.INT, 0)
				.add("y", SerializableDataTypes.INT, 0)
				.add("z", SerializableDataTypes.INT, 0),
			(data, block) -> ((ConditionTypeFactory<BlockInWorld>) data.get("condition"))
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
