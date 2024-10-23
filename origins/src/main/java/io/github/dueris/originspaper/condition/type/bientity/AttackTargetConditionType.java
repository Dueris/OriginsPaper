package io.github.dueris.originspaper.condition.type.bientity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Targeting;

import java.util.Objects;

public class AttackTargetConditionType {

	public static boolean condition(Entity actor, Entity target) {
		return (actor instanceof Targeting targeter && Objects.equals(target, targeter.getTarget()))
			|| (actor instanceof NeutralMob angerable && Objects.equals(target, angerable.getTarget()));
	}

}
