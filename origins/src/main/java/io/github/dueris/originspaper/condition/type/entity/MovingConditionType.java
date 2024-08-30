package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.factory.EntityConditions;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class MovingConditionType {

	public static boolean condition(Entity entity, boolean horizontally, boolean vertically) {
		return (horizontally && EntityConditions.isEntityMovingHorizontal(entity))
			|| (vertically && EntityConditions.isEntityMovingVertical(entity));
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("moving"),
			new SerializableData()
				.add("horizontally", SerializableDataTypes.BOOLEAN, true)
				.add("vertically", SerializableDataTypes.BOOLEAN, true),
			(data, entity) -> condition(entity,
				data.get("horizontally"),
				data.get("vertically")
			)
		);
	}

}
