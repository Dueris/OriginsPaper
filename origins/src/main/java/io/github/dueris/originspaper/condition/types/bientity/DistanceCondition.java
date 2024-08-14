package io.github.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DistanceCondition {

	public static boolean condition(DeserializedFactoryJson data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

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

	public static @NotNull ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("distance"),
			InstanceDefiner.instanceDefiner()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.DOUBLE),
			DistanceCondition::condition
		);
	}
}
