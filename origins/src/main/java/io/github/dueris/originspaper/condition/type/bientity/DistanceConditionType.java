package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DistanceConditionType {

	public static boolean condition(Entity actor, Entity target, Comparison comparison, double compareTo) {
		return actor != null
			&& target != null
			&& comparison.compare(actor.position().distanceToSqr(target.position()), compareTo * compareTo);
	}

	public static @NotNull ConditionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("distance"),
			new SerializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.DOUBLE),
			(data, actorAndTarget) -> condition(actorAndTarget.getA(), actorAndTarget.getB(),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
