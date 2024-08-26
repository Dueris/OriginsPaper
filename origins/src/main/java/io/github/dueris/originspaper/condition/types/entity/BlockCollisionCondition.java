package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class BlockCollisionCondition {

	public static boolean condition(@NotNull SerializableData.Instance data, @NotNull Entity entity) {

		AABB entityBoundingBox = entity.getBoundingBox();
		AABB offsetEntityBoundingBox = entityBoundingBox.move(
			data.getFloat("offset_x") * entityBoundingBox.getXsize(),
			data.getFloat("offset_y") * entityBoundingBox.getYsize(),
			data.getFloat("offset_z") * entityBoundingBox.getZsize()
		);

		Predicate<BlockInWorld> blockCondition = data.get("block_condition");
		Level world = entity.level();

		BlockCollisions<BlockPos> spliterator = new BlockCollisions<>(entity.level(), entity, offsetEntityBoundingBox, false, (pos, shape) -> pos);

		while (spliterator.hasNext()) {

			BlockPos blockPos = spliterator.next();

			if (blockCondition == null || blockCondition.test(new BlockInWorld(world, blockPos, true))) {
				return true;
			}

		}

		return false;

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("block_collision"),
			SerializableData.serializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("offset_x", SerializableDataTypes.FLOAT, 0F)
				.add("offset_y", SerializableDataTypes.FLOAT, 0F)
				.add("offset_z", SerializableDataTypes.FLOAT, 0F),
			BlockCollisionCondition::condition
		);
	}
}
