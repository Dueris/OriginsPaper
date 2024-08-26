package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class ExposedToSkyCondition {

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("exposed_to_sky"),
			SerializableData.serializableData(),
			(data, block) -> {
				return block.getLevel().canSeeSky(block.getPos());
			}
		);
	}
}
