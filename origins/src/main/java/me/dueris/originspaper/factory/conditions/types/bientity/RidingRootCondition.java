package me.dueris.originspaper.factory.conditions.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import net.minecraft.util.Tuple;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.entity.Entity;

public class RidingRootCondition {

	public static boolean condition(DeserializedFactoryJson data, Tuple<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getA();
		Entity target = actorAndTarget.getB();

		if ((actor == null || target == null) || !actor.isPassenger()) {
			return false;
		}

		return actor.getRootVehicle().equals(target);

	}

	public static ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("riding_root"),
			InstanceDefiner.instanceDefiner(),
			RidingRootCondition::condition
		);
	}
}
