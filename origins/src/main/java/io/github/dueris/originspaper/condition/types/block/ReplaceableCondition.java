package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class ReplaceableCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("replaceable"),
			InstanceDefiner.instanceDefiner(),
			(data, block) -> {
				return block.getState().canBeReplaced();
			}
		);
	}
}
