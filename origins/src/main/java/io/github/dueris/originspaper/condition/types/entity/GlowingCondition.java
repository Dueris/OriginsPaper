package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class GlowingCondition {

	public static boolean condition(DeserializedFactoryJson data, @NotNull Entity entity) {
		return !entity.level().isClientSide && entity.isCurrentlyGlowing();
	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("glowing"),
			InstanceDefiner.instanceDefiner(),
			GlowingCondition::condition
		);
	}
}
