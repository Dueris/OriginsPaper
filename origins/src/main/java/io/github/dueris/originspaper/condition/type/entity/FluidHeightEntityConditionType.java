package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.access.SubmergableEntity;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class FluidHeightEntityConditionType extends EntityConditionType {

	public static final TypedDataObjectFactory<FluidHeightEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("fluid", SerializableDataTypes.FLUID_TAG)
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataTypes.DOUBLE),
		data -> new FluidHeightEntityConditionType(
			data.get("fluid"),
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("fluid", conditionType.fluidTag)
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final TagKey<Fluid> fluidTag;

	private final Comparison comparison;
	private final double compareTo;

	public FluidHeightEntityConditionType(TagKey<Fluid> fluidTag, Comparison comparison, double compareTo) {
		this.fluidTag = fluidTag;
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(Entity entity) {
		return comparison.compare(((SubmergableEntity) entity).apoli$getFluidHeightLoosely(fluidTag), compareTo);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.FLUID_HEIGHT;
	}

}
