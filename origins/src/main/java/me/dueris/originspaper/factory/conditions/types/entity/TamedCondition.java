package me.dueris.originspaper.factory.conditions.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import org.jetbrains.annotations.NotNull;

public class TamedCondition {

	public static boolean condition(DeserializedFactoryJson data, Entity entity) {
		return entity instanceof OwnableEntity tameable
			&& tameable.getOwnerUUID() != null;
	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("tamed"),
			InstanceDefiner.instanceDefiner(),
			TamedCondition::condition
		);
	}
}
