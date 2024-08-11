package me.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RidingRecursiveCondition {

	public static boolean condition(DeserializedFactoryJson data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

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

	public static @NotNull ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("riding_recursive"),
			InstanceDefiner.instanceDefiner(),
			RidingRecursiveCondition::condition
		);
	}
}
