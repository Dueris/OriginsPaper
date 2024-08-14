package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityTypeCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("entity_type"),
			InstanceDefiner.instanceDefiner()
				.add("entity_type", SerializableDataTypes.ENTITY_TYPE),
			(data, entity) -> {
				return entity.getType() == data.get("entity_type");
			}
		);
	}
}
