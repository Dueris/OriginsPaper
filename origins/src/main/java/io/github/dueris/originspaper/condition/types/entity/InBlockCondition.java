package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class InBlockCondition {

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("in_block"),
			SerializableData.serializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION),
			(data, entity) -> {
				return ((ConditionTypeFactory<BlockInWorld>) data.get("block_condition")).test(
					new BlockInWorld(entity.level(), entity.blockPosition(), true));
			}
		);
	}
}
