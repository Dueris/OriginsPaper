package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public class InThunderstormConditionType {

	public static boolean condition(Entity entity) {

		BlockPos downBlockPos = entity.blockPosition();
		BlockPos upBlockPos = BlockPos.containing(downBlockPos.getX(), entity.getBoundingBox().maxY, downBlockPos.getZ());

		return Util.inThunderstorm(entity.level(), downBlockPos, upBlockPos);

	}

}
