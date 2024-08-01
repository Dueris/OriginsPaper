package me.dueris.originspaper.factory.condition.types.block;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class WaterLoggableCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("water_loggable"),
			InstanceDefiner.instanceDefiner(),
			(data, block) -> {
				return block.getState().getBlock() instanceof LiquidBlockContainer;
			}
		);
	}
}
