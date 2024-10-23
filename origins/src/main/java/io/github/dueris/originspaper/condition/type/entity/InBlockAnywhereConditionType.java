package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;

import java.util.function.Predicate;

public class InBlockAnywhereConditionType {

	public static boolean condition(Entity entity, Predicate<BlockInWorld> blockCondition, Comparison comparison, int compareTo) {

		AABB boundingBox = entity.getBoundingBox();
		int countThreshold = switch (comparison) {
			case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> compareTo + 1;
			case LESS_THAN, GREATER_THAN_OR_EQUAL -> compareTo;
			default -> -1;
		};

		BlockPos minPos = BlockPos.containing(boundingBox.minX + 0.001D, boundingBox.minY + 0.001D, boundingBox.minZ + 0.001D);
		BlockPos maxPos = BlockPos.containing(boundingBox.maxX - 0.001D, boundingBox.maxY - 0.001D, boundingBox.maxZ - 0.001D);

		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
		int count = 0;

		for (int x = minPos.getX(); x <= maxPos.getX() && count < countThreshold; x++) {
			for (int y = minPos.getY(); y <= maxPos.getY() && count < countThreshold; y++) {
				for (int z = minPos.getZ(); z <= maxPos.getZ() && count < countThreshold; z++) {

					mutablePos.set(x, y, z);

					if (blockCondition.test(new BlockInWorld(entity.level(), mutablePos, true))) {
						count++;
					}

				}
			}
		}

		return comparison.compare(count, compareTo);

	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("in_block_anywhere"),
			new SerializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
				.add("compare_to", SerializableDataTypes.INT, 1),
			(data, entity) -> condition(entity,
				data.get("block_condition"),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
