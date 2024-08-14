package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class LightBlockingCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("light_blocking"),
			InstanceDefiner.instanceDefiner(),
			(data, block) -> {
				return block.getState().canOcclude();
			}
		);
	}
}
