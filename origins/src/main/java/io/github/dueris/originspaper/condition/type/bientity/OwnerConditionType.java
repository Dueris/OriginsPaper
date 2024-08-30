package io.github.dueris.originspaper.condition.type.bientity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;

import java.util.Objects;

public class OwnerConditionType {

	public static boolean condition(Entity actor, Entity target) {
		return (target instanceof OwnableEntity tameable && Objects.equals(actor, tameable.getOwner()))
			|| (target instanceof TraceableEntity ownable && Objects.equals(actor, ownable.getOwner()));
	}

}
