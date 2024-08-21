package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import org.jetbrains.annotations.NotNull;

public class TamedCondition {

	public static boolean condition(SerializableData.Instance data, Entity entity) {
		return entity instanceof OwnableEntity tameable
			&& tameable.getOwnerUUID() != null;
	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("tamed"),
			SerializableData.serializableData(),
			TamedCondition::condition
		);
	}
}
