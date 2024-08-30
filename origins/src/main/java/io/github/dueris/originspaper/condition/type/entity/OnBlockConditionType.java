package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class OnBlockConditionType {

	public static boolean condition(@NotNull Entity entity, Predicate<BlockInWorld> blockCondition) {
		BlockPos pos = BlockPos.containing(entity.getX(), entity.getBoundingBox().minY - 0.5000001D, entity.getY());
		return entity.onGround()
			&& blockCondition.test(new BlockInWorld(entity.level(), pos, true));
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("on_block"),
			new SerializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
			(data, entity) -> condition(entity,
				data.getOrElse("block_condition", cachedBlock -> true)
			)
		);
	}

}
