package io.github.dueris.originspaper.condition.type.fluid;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class InTagConditionType {

	public static boolean condition(FluidState fluidState, TagKey<Fluid> fluidTag) {
		return fluidState.is(fluidTag);
	}

	public static ConditionTypeFactory<FluidState> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("in_tag"),
			new SerializableData()
				.add("tag", SerializableDataTypes.FLUID_TAG),
			(data, fluidState) -> condition(fluidState,
				data.get("tag")
			)
		);
	}

}
