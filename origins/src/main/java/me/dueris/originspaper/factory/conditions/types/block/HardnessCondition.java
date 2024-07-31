package me.dueris.originspaper.factory.conditions.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.factory.data.types.Comparison;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class HardnessCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("hardness"),
			InstanceDefiner.instanceDefiner()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.FLOAT),
			(data, block) -> {
				BlockState state = block.getState();
				return ((Comparison) data.get("comparison")).compare(state.getBlock().defaultDestroyTime(), data.getFloat("compare_to"));
			}
		);
	}
}
