package me.dueris.originspaper.factory.conditions.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.entity.Entity;

public class RidingRecursiveCondition {

	public static boolean condition(DeserializedFactoryJson data, Pair<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getA();
		Entity target = actorAndTarget.getB();

		if ((actor == null || target == null) || !actor.isPassenger()) {
			return false;
		}

		Entity vehicle = actor.getVehicle();
		while (vehicle != null && !vehicle.equals(target)) {
			vehicle = vehicle.getVehicle();
		}

		return target.equals(vehicle);

	}

	public static ConditionFactory<Pair<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("riding_recursive"),
			InstanceDefiner.instanceDefiner(),
			RidingRecursiveCondition::condition
		);
	}
}
