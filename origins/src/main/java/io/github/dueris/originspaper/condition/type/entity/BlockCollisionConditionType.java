package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.BlockCollisionSpliteratorAccess;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class BlockCollisionConditionType {

	public static boolean condition(Entity entity, Predicate<BlockInWorld> blockCondition, Vec3 offset) {

		AABB boundingBox = entity.getBoundingBox().move(offset);
		Level world = entity.level();

		BlockCollisions<BlockPos> spliterator = new BlockCollisions<>(world, entity, boundingBox, false, (pos, shape) -> pos);
		((BlockCollisionSpliteratorAccess) spliterator).apoli$setGetOriginalShapes(true);

		while (spliterator.hasNext()) {

			BlockPos pos = spliterator.next();

			if (blockCondition.test(new BlockInWorld(world, pos, true))) {
				return true;
			}

		}

		return false;

	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("block_collision"),
			new SerializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("offset_x", SerializableDataTypes.DOUBLE, 0.0)
				.add("offset_y", SerializableDataTypes.DOUBLE, 0.0)
				.add("offset_z", SerializableDataTypes.DOUBLE, 0.0),
			(data, entity) -> condition(entity,
				data.getOrElse("block_condition", cachedBlock -> true),
				new Vec3(data.get("offset_x"), data.get("offset_y"), data.get("offset_z"))
			)
		);
	}

}
