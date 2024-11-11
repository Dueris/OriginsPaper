package io.github.dueris.originspaper.condition.type.fluid.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.FluidConditionType;
import io.github.dueris.originspaper.condition.type.FluidConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceMetaConditionType;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class RandomChanceFluidConditionType extends FluidConditionType implements RandomChanceMetaConditionType {

	private final float chance;

	public RandomChanceFluidConditionType(float chance) {
		this.chance = chance;
	}

	@Override
	public boolean test(FluidState fluidState) {
		return testCondition();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return FluidConditionTypes.RANDOM_CHANCE;
	}

	@Override
	public float chance() {
		return chance;
	}

}
