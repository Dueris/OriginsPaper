package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class InTagConditionType {

	public static boolean condition(Entity entity, TagKey<EntityType<?>> entityTag) {
		return entity.getType().is(entityTag);
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("in_tag"),
			new SerializableData()
				.add("tag", SerializableDataTypes.ENTITY_TAG),
			(data, entity) -> condition(entity,
				data.get("tag")
			)
		);
	}

}
