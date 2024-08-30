package io.github.dueris.originspaper.condition.type.bientity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;

public class AttackTargetConditionType {

	public static boolean condition(Entity actor, Entity target) {

		if (actor == null || target == null) {
			return false;
		}

		return (actor instanceof Mob mobActor && target.equals(mobActor.getTarget()))
			|| (actor instanceof NeutralMob angerableActor && target.equals(angerableActor.getTarget()));

	}

}
