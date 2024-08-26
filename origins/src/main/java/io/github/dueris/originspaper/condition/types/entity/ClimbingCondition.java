package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ClimbingCondition {

	public static boolean condition(SerializableData.Instance data, Entity entity) {
		return entity instanceof LivingEntity livingEntity && livingEntity.onClimbable();
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("climbing"),
			SerializableData.serializableData(),
			ClimbingCondition::condition
		);
	}
}
