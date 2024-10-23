package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;

public class FluidHeightConditionType {

	public static boolean condition(Entity entity, TagKey<Fluid> fluidTag, Comparison comparison, double compareTo) {
		return comparison.compare(Util.apoli$getFluidHeightLoosely(entity, fluidTag), compareTo);
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("fluid_height"),
			new SerializableData()
				.add("fluid", SerializableDataTypes.FLUID_TAG)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.DOUBLE),
			(data, entity) -> condition(entity,
				data.get("fluid"),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
