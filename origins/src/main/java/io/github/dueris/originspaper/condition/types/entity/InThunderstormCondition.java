package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class InThunderstormCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Entity entity) {

		BlockPos downBlockPos = entity.blockPosition();
		BlockPos upBlockPos = BlockPos.containing(downBlockPos.getX(), entity.getBoundingBox().maxY, downBlockPos.getX());

		return Util.inThunderstorm(entity.level(), downBlockPos, upBlockPos);

	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("in_thunderstorm"),
			SerializableData.serializableData(),
			InThunderstormCondition::condition
		);
	}
}
