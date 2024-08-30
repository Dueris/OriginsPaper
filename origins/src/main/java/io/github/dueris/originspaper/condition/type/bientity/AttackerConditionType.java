package io.github.dueris.originspaper.condition.type.bientity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AttackerConditionType {

	public static boolean condition(Entity actor, Entity target) {

		if (actor == null || target == null) {
			return false;
		}

		return target instanceof LivingEntity livingTarget
			&& actor.equals(livingTarget.getLastHurtByMob());

	}

}
