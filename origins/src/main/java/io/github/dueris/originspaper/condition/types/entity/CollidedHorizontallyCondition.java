package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class CollidedHorizontallyCondition {

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("collided_horizontally"),
			SerializableData.serializableData(),
			(data, entity) -> entity.horizontalCollision
		);
	}
}
