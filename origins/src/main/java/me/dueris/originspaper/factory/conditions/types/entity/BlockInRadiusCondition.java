package me.dueris.originspaper.factory.conditions.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.factory.data.types.Comparison;
import me.dueris.originspaper.factory.data.types.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class BlockInRadiusCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("block_in_radius"),
			InstanceDefiner.instanceDefiner()
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
