package me.dueris.originspaper.factory.condition.types.block;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class AttachableCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("attachable"),
			InstanceDefiner.instanceDefiner(),
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
