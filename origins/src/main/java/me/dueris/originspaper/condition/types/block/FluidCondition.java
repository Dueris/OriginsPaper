package me.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class FluidCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("fluid"),
			InstanceDefiner.instanceDefiner()
				.add("fluid_condition", ApoliDataTypes.FLUID_CONDITION),
			(data, block) -> ((ConditionFactory<FluidState>) data.get("fluid_condition")).test(block.getLevel().getFluidState(block.getPos()))
		);
	}
}
