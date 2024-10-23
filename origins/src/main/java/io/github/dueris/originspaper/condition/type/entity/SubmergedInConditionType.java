package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;

public class SubmergedInConditionType {

	public static boolean condition(Entity entity, TagKey<Fluid> fluidTag) {
		return Util.apoli$isSubmergedInLoosely(entity, fluidTag);
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("submerged_in"),
			new SerializableData()
				.add("fluid", SerializableDataTypes.FLUID_TAG),
			(data, entity) -> condition(entity,
				data.get("fluid")
			)
		);
	}

}
