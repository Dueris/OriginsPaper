package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SubmergedInCondition {

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("submerged_in"),
			SerializableData.serializableData()
				.add("fluid", SerializableDataTypes.FLUID_TAG),
			(data, entity) -> {
				return Util.apoli$isSubmergedInLoosely(entity, data.get("fluid"));
			}
		);
	}
}
