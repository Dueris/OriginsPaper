package io.github.dueris.originspaper.condition.type.bientity;

import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class RidingRecursiveConditionType {

	public static boolean condition(Entity actor, Entity target) {

		if (actor == null || target == null || !actor.isPassenger()) {
			return false;
		}

		Entity vehicle = actor.getVehicle();
		while (vehicle != null) {

			if (Objects.equals(vehicle, target)) {
				return true;
			}

			vehicle = vehicle.getVehicle();

		}

		return false;

	}

}
