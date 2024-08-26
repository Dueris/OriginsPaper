package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class AdjacentCondition {

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("adjacent"),
			SerializableData.serializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT)
				.add("adjacent_condition", ApoliDataTypes.BLOCK_CONDITION),
			(data, block) -> {
				ConditionTypeFactory<BlockInWorld> adjacentCondition = data.get("adjacent_condition");
				int adjacent = 0;
				for (Direction d : Direction.values()) {
					if (adjacentCondition.test(new BlockInWorld(block.getLevel(), block.getPos().relative(d), true))) {
						adjacent++;
					}
				}
				return ((Comparison) data.get("comparison")).compare(adjacent, data.getInt("compare_to"));
			}
		);
	}
}
