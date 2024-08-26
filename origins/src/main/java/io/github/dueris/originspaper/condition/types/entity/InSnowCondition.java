package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class InSnowCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Entity entity) {

		BlockPos downBlockPos = entity.blockPosition();
		BlockPos upBlockPos = BlockPos.containing(downBlockPos.getX(), entity.getBoundingBox().maxY, downBlockPos.getX());

		return Util.inSnow(entity.level(), downBlockPos, upBlockPos);

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("in_snow"),
			SerializableData.serializableData(),
			InSnowCondition::condition
		);
	}
}
