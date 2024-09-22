package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DimensionConditionType {

	public static boolean condition(@NotNull Entity entity, ResourceKey<Level> dimensionKey) {
		return entity.level().dimension().equals(dimensionKey);
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("dimension"),
			new SerializableData()
				.add("dimension", SerializableDataTypes.DIMENSION),
			(data, entity) -> condition(entity,
				data.get("dimension")
			)
		);
	}

}
