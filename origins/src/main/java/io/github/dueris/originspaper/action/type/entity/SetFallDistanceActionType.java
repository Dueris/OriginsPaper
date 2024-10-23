package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.entity.Entity;

public class SetFallDistanceActionType {

	public static void action(Entity entity, float fallDistance) {
		entity.fallDistance = fallDistance;
	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("set_fall_distance"),
			new SerializableData()
				.add("fall_distance", SerializableDataTypes.FLOAT),
			(data, entity) -> action(entity,
				data.get("fall_distance")
			)
		);
	}

}
