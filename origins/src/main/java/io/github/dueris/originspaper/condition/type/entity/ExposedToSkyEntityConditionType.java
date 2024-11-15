package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ExposedToSkyEntityConditionType extends EntityConditionType {

	@Override
	public boolean test(Entity entity) {
		Level world = entity.level();
		return world.canSeeSky(BlockPos.containing(entity.getEyePosition()))
			|| world.canSeeSky(entity.blockPosition());
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.EXPOSED_TO_SKY;
	}

}
