package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.function.Predicate;

public class InBlockConditionType {

	public static boolean condition(Entity entity, Predicate<BlockInWorld> blockCondition) {
		return blockCondition.test(new BlockInWorld(entity.level(), entity.blockPosition(), true));
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("in_block"),
			new SerializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION),
			(data, entity) -> condition(entity,
				data.get("block_condition")
			)
		);
	}

}
