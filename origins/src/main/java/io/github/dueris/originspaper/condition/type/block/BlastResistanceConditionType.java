package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class BlastResistanceConditionType {

	public static boolean condition(BlockState blockState, Comparison comparison, float compareTo) {
		return comparison.compare(blockState.getBlock().getExplosionResistance(), compareTo);
	}

	public static ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("blast_resistance"),
			new SerializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.FLOAT),
			(data, cachedBlock) -> condition(cachedBlock.getState(),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
