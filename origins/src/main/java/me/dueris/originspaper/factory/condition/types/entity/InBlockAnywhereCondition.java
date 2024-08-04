package me.dueris.originspaper.factory.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.Comparison;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class InBlockAnywhereCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("in_block_anywhere"),
			InstanceDefiner.instanceDefiner()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
				.add("compare_to", SerializableDataTypes.INT, 1),
			(data, entity) -> {
				Predicate<BlockInWorld> blockCondition = data.get("block_condition");
				int stopAt = -1;
				Comparison comparison = data.get("comparison");
				int compareTo = data.getInt("compare_to");
				switch (comparison) {
					case EQUAL:
					case LESS_THAN_OR_EQUAL:
					case GREATER_THAN:
					case NOT_EQUAL:
						stopAt = compareTo + 1;
						break;
					case LESS_THAN:
					case GREATER_THAN_OR_EQUAL:
						stopAt = compareTo;
						break;
				}
				int count = 0;
				AABB box = entity.getBoundingBox();
				BlockPos blockPos = BlockPos.containing(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
				BlockPos blockPos2 = BlockPos.containing(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);
				BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
				for (int i = blockPos.getX(); i <= blockPos2.getX() && count < stopAt; ++i) {
					for (int j = blockPos.getY(); j <= blockPos2.getY() && count < stopAt; ++j) {
						for (int k = blockPos.getZ(); k <= blockPos2.getZ() && count < stopAt; ++k) {
							mutable.set(i, j, k);
							if (blockCondition.test(new BlockInWorld(entity.level(), mutable, true))) {
								count++;
							}
						}
					}
				}
				return comparison.compare(count, compareTo);
			}
		);
	}
}
