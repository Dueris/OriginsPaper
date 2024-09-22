package io.github.dueris.originspaper.condition.type.fluid;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class FluidConditionType {

	public static boolean condition(FluidState fluidState, Fluid fluid) {
		return fluidState.getType() == fluid;
	}

	public static ConditionTypeFactory<FluidState> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("fluid"),
			new SerializableData()
				.add("fluid", SerializableDataTypes.FLUID),
			(data, fluidState) -> condition(fluidState,
				data.get("fluid")
			)
		);
	}

}
