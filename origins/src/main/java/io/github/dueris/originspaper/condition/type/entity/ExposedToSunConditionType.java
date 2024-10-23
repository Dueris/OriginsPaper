package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ExposedToSunConditionType {

	public static boolean condition(Entity entity) {

		Level world = entity.level();
		BlockPos pos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());

		return world.isDay()
			&& !InRainConditionType.condition(entity)
			&& BrightnessConditionType.condition(entity, Comparison.GREATER_THAN, 0.5F)
			&& world.canSeeSky(pos);

	}

}
