package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class AdjacentConditionType {

	public static boolean condition(BlockInWorld cachedBlock, Predicate<BlockInWorld> adjacentCondition, Comparison comparison, int compareTo) {

		int matchingAdjacents = 0;
		for (Direction direction : Direction.values()) {

			if (adjacentCondition.test(new BlockInWorld(cachedBlock.getLevel(), cachedBlock.getPos().relative(direction), true))) {
				matchingAdjacents++;
			}

		}

		return comparison.compare(matchingAdjacents, compareTo);

	}

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("adjacent"),
			new SerializableData()
				.add("adjacent_condition", ApoliDataTypes.BLOCK_CONDITION)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			(data, cachedBlock) -> condition(cachedBlock,
				data.get("adjacent_condition"),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
