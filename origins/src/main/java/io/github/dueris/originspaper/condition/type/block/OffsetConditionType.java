package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class OffsetConditionType {

	public static boolean condition(@NotNull BlockInWorld cachedBlock, @NotNull Predicate<BlockInWorld> blockCondition, Vec3i offset) {
		return blockCondition.test(new BlockInWorld(cachedBlock.getLevel(), cachedBlock.getPos().offset(offset), true));
	}

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("offset"),
			new SerializableData()
				.add("condition", ApoliDataTypes.BLOCK_CONDITION)
				.add("x", SerializableDataTypes.INT, 0)
				.add("y", SerializableDataTypes.INT, 0)
				.add("z", SerializableDataTypes.INT, 0),
			(data, cachedBlock) -> condition(cachedBlock,
				data.get("condition"),
				new Vec3i(data.get("x"), data.get("y"), data.get("z"))
			)
		);
	}

}
