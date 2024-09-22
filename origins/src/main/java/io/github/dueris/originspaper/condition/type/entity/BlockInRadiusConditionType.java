package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import io.github.dueris.originspaper.data.types.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class BlockInRadiusConditionType {

	public static boolean condition(Entity entity, Predicate<BlockInWorld> blockCondition, int radius, Shape shape, @NotNull Comparison comparison, int compareTo) {

		int countThreshold = switch (comparison) {
			case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> compareTo + 1;
			case LESS_THAN, GREATER_THAN_OR_EQUAL -> compareTo;
			default -> -1;
		};

		int count = 0;
		for (BlockPos pos : Shape.getPositions(entity.blockPosition(), shape, radius)) {

			if (blockCondition.test(new BlockInWorld(entity.level(), pos, true))) {
				++count;
			}

			if (count == countThreshold) {
				break;
			}

		}

		return comparison.compare(count, compareTo);

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("block_in_radius"),
			new SerializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION)
				.add("radius", SerializableDataTypes.INT)
				.add("shape", SerializableDataTypes.enumValue(Shape.class), Shape.CUBE)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
				.add("compare_to", SerializableDataTypes.INT, 1),
			(data, entity) -> condition(entity,
				data.get("block_condition"),
				data.get("radius"),
				data.get("shape"),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
