package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import io.github.dueris.originspaper.data.types.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class BlockInRadiusCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
				OriginsPaper.apoliIdentifier("block_in_radius"),
				SerializableData.serializableData()
						.add("block_condition", ApoliDataTypes.BLOCK_CONDITION)
						.add("radius", SerializableDataTypes.INT)
						.add("shape", SerializableDataTypes.enumValue(Shape.class), Shape.CUBE)
						.add("compare_to", SerializableDataTypes.INT, 1)
						.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
				(data, entity) -> {
					Predicate<BlockInWorld> blockCondition = data.get("block_condition");
					int stopAt = -1;
					Comparison comparison = data.get("comparison");
					int compareTo = data.getInt("compare_to");
					switch (comparison) {
						case EQUAL:
						case LESS_THAN_OR_EQUAL:
						case GREATER_THAN:
							stopAt = compareTo + 1;
							break;
						case LESS_THAN:
						case GREATER_THAN_OR_EQUAL:
							stopAt = compareTo;
							break;
					}
					int count = 0;
					for (BlockPos pos : Shape.getPositions(entity.blockPosition(), data.get("shape"), data.getInt("radius"))) {
						if (blockCondition.test(new BlockInWorld(entity.level(), pos, true))) {
							count++;
							if (count == stopAt) {
								break;
							}
						}
					}
					return comparison.compare(count, compareTo);
				}
		);
	}
}
