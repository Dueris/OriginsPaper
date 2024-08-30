package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class HeightConditionType {

	public static boolean condition(@NotNull BlockInWorld cachedBlock, @NotNull Comparison comparison, int compareTo) {
		return comparison.compare(cachedBlock.getPos().getY(), compareTo);
	}

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("height"),
			new SerializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			(data, cachedBlock) -> condition(cachedBlock,
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
