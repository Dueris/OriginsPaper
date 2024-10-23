package io.github.dueris.originspaper.condition.type.bientity;

import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class AttackerConditionType {

	public static boolean condition(Entity actor, Entity target) {
		return target instanceof Attackable attackable
			&& Objects.equals(actor, attackable.getLastAttacker());
	}

}
