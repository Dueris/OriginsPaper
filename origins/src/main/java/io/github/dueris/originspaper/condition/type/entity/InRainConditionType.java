package io.github.dueris.originspaper.condition.type.entity;

import net.minecraft.world.entity.Entity;

public class InRainConditionType {

	public static boolean condition(Entity entity) {
		return entity.isInRain();
	}

}
