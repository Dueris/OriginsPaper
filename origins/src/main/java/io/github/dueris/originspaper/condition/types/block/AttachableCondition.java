package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class AttachableCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
				OriginsPaper.apoliIdentifier("attachable"),
				SerializableData.serializableData(),
				(data, block) -> {
					for (Direction d : Direction.values()) {
						BlockPos adjacent = block.getPos().relative(d);
						if (block.getLevel().getBlockState(adjacent).isFaceSturdy(block.getLevel(), block.getPos(), d.getOpposite())) {
							return true;
						}
					}
					return false;
				}
		);
	}
}
