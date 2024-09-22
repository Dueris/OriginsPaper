package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class FluidConditionType {

	public static boolean condition(@NotNull BlockInWorld cachedBlock, @NotNull Predicate<FluidState> fluidCondition) {
		return fluidCondition.test(cachedBlock.getLevel().getFluidState(cachedBlock.getPos()));
	}

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("fluid"),
			new SerializableData()
				.add("fluid_condition", ApoliDataTypes.FLUID_CONDITION),
			(data, cachedBlock) -> condition(cachedBlock,
				data.get("fluid_condition")
			)
		);
	}

}
