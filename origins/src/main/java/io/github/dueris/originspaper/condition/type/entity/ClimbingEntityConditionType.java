package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ClimbingEntityConditionType extends EntityConditionType {

	@Override
	public boolean test(Entity entity) {
		return entity instanceof LivingEntity livingEntity
			&& livingEntity.onClimbable();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.CLIMBING;
	}

}
