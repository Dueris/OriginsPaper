package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class FluidHeightCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("fluid_height"),
			InstanceDefiner.instanceDefiner()
				.add("fluid", SerializableDataTypes.FLUID_TAG)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.DOUBLE),
			(data, entity) -> {
				return ((Comparison) data.get("comparison")).compare(Util.apoli$getFluidHeightLoosely(entity, data.get("fluid")), data.getDouble("compare_to"));
			}
		);
	}
}
