package io.github.dueris.originspaper.condition.type.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ExposedToSkyConditionType {

	public static boolean condition(Entity entity) {

		Level world = entity.level();
		BlockPos pos = BlockPos.containing(entity.position());

		return world.canSeeSky(pos.above())
			|| world.canSeeSky(pos);

	}

}
