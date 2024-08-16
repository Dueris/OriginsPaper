package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DaytimeCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
				OriginsPaper.apoliIdentifier("daytime"),
				SerializableData.serializableData(),
				(data, entity) -> {
					return entity.level().getDayTime() % 24000L < 13000L;
				}
		);
	}
}
