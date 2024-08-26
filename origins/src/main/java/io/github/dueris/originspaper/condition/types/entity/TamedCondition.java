package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import org.jetbrains.annotations.NotNull;

public class TamedCondition {

	public static boolean condition(SerializableData.Instance data, Entity entity) {
		return entity instanceof OwnableEntity tameable
			&& tameable.getOwnerUUID() != null;
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("tamed"),
			SerializableData.serializableData(),
			TamedCondition::condition
		);
	}
}
