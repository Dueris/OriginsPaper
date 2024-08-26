package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class InTagCondition {

	public static boolean condition(@NotNull SerializableData.Instance data, @NotNull Entity entity) {
		TagKey<EntityType<?>> entityTypeTag = data.get("tag");
		return entity.getType().is(entityTypeTag);
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("in_tag"),
			SerializableData.serializableData()
				.add("tag", SerializableDataTypes.ENTITY_TAG),
			InTagCondition::condition
		);
	}
}
