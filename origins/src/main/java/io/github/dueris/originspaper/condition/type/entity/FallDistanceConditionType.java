package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.world.entity.Entity;

public class FallDistanceConditionType {

	public static boolean condition(Entity entity, Comparison comparison, float compareTo) {
		return comparison.compare(entity.fallDistance, compareTo);
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("fall_distance"),
			new SerializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.FLOAT),
			(data, entity) -> condition(entity,
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
