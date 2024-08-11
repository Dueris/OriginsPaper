package me.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.Comparison;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class AdjacentCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("adjacent"),
			InstanceDefiner.instanceDefiner()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT)
				.add("adjacent_condition", ApoliDataTypes.BLOCK_CONDITION),
			(data, block) -> {
				ConditionFactory<BlockInWorld> adjacentCondition = data.get("adjacent_condition");
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
