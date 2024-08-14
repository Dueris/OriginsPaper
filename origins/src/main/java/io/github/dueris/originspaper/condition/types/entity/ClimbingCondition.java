package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ClimbingCondition {

	public static boolean condition(DeserializedFactoryJson data, Entity entity) {
		return entity instanceof LivingEntity livingEntity && livingEntity.onClimbable();
	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("climbing"),
			InstanceDefiner.instanceDefiner(),
			ClimbingCondition::condition
		);
	}
}
