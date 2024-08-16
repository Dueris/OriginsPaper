package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class GlowingCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Entity entity) {
		return !entity.level().isClientSide && entity.isCurrentlyGlowing();
	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
				OriginsPaper.apoliIdentifier("glowing"),
				SerializableData.serializableData(),
				GlowingCondition::condition
		);
	}
}
