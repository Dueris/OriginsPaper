package io.github.dueris.originspaper.condition.type.bientity;

import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class RidingRootConditionType {

	public static boolean condition(Entity actor, Entity target) {
		return actor != null
			&& target != null
			&& Objects.equals(actor.getRootVehicle(), target);
	}

}
