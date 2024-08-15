package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DimensionCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("dimension"),
			SerializableData.serializableData()
				.add("dimension", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				return entity.level().dimension() == ResourceKey.create(Registries.DIMENSION, data.getId("dimension"));
			}
		);
	}
}
