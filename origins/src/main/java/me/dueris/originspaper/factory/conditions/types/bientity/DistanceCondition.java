package me.dueris.originspaper.factory.conditions.types.bientity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import net.minecraft.util.Tuple;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.factory.data.types.Comparison;
import net.minecraft.world.entity.Entity;

public class DistanceCondition {

	public static boolean condition(DeserializedFactoryJson data, Tuple<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getB();
		Entity target = actorAndTarget.getA();

		if (actor == null || target == null) {
			return false;
		}

		Comparison comparison = data.get("comparison");
		double compareTo = data.get("compare_to");

		compareTo *= compareTo;
		return comparison.compare(actor.position().distanceToSqr(target.position()), compareTo);

	}

	public static ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("distance"),
			InstanceDefiner.instanceDefiner()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.DOUBLE),
			DistanceCondition::condition
		);
	}
}
