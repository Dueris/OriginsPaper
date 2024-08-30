package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class EntityTypeConditionType {

	public static boolean condition(@NotNull Entity entity, EntityType<?> entityType) {
		return entity.getType().equals(entityType);
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("entity_type"),
			new SerializableData()
				.add("entity_type", SerializableDataTypes.ENTITY_TYPE),
			(data, entity) -> condition(entity,
				data.get("entity_type")
			)
		);
	}

}
