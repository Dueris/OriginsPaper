package me.dueris.originspaper.factory.conditions.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class OffsetCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("offset"),
			InstanceDefiner.instanceDefiner()
				.add("condition", ApoliDataTypes.BLOCK_CONDITION)
				.add("x", SerializableDataTypes.INT, 0)
				.add("y", SerializableDataTypes.INT, 0)
				.add("z", SerializableDataTypes.INT, 0),
			(data, block) -> ((ConditionFactory<BlockInWorld>) data.get("condition"))
				.test(new BlockInWorld(
					block.getLevel(),
					block.getPos().offset(
						data.getInt("x"),
						data.getInt("y"),
						data.getInt("z")
					), true))
		);
	}
}
