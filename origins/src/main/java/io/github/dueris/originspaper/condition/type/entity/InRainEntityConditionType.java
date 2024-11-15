package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class InRainEntityConditionType extends EntityConditionType {

	public static boolean condition(Entity entity) {
		return entity.isInRain();
	}

	@Override
	public boolean test(Entity entity) {
		return entity.isInRain();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.IN_RAIN;
	}

}
