package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class OnBlockCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("on_block"),
			SerializableData.serializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
			(data, entity) -> {
				return entity.onGround() &&
					(!data.isPresent("block_condition") || ((ConditionFactory<BlockInWorld>) data.get("block_condition")).test(
						new BlockInWorld(entity.level(), BlockPos.containing(entity.getX(), entity.getBoundingBox().minY - 0.5000001D, entity.getZ()), true)));
			}
		);
	}
}
