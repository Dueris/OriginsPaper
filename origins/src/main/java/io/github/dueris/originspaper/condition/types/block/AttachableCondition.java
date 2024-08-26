package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class AttachableCondition {

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
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
