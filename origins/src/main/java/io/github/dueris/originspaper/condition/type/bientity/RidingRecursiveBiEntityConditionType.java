package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RidingRecursiveBiEntityConditionType extends BiEntityConditionType {

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

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiEntityConditionTypes.RIDING_RECURSIVE;
	}

	@Override
	public boolean test(Entity actor, Entity target) {
		return condition(actor, target);
	}

}
