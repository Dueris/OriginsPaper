package io.github.dueris.originspaper.condition.type.fluid.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.FluidConditionType;
import io.github.dueris.originspaper.condition.type.FluidConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.ConstantMetaConditionType;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class ConstantFluidConditionType extends FluidConditionType implements ConstantMetaConditionType {

	private final boolean value;

	public ConstantFluidConditionType(boolean value) {
		this.value = value;
	}

	@Override
	public boolean test(FluidState fluidState) {
		return value();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return FluidConditionTypes.CONSTANT;
	}

	@Override
	public boolean value() {
		return value;
	}

}
