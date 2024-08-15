package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class FluidCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("fluid"),
			SerializableData.serializableData()
				.add("fluid_condition", ApoliDataTypes.FLUID_CONDITION),
			(data, block) -> ((ConditionFactory<FluidState>) data.get("fluid_condition")).test(block.getLevel().getFluidState(block.getPos()))
		);
	}
}
