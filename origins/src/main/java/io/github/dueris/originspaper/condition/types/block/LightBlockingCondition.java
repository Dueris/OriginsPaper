package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class LightBlockingCondition {

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("light_blocking"),
			SerializableData.serializableData(),
			(data, block) -> {
				return block.getState().canOcclude();
			}
		);
	}
}
