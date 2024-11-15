package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.originspaper.util.PowerUtil;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ResourceEntityConditionType extends EntityConditionType {

	public static final TypedDataObjectFactory<ResourceEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("resource", ApoliDataTypes.RESOURCE_REFERENCE)
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataTypes.INT),
		data -> new ResourceEntityConditionType(
			data.get("resource"),
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("resource", conditionType.resource)
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final PowerReference resource;

	private final Comparison comparison;
	private final int compareTo;

	public ResourceEntityConditionType(PowerReference resource, Comparison comparison, int compareTo) {
		this.resource = resource;
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(Entity entity) {
		return comparison.compare(PowerUtil.getResourceValue(resource.getPowerTypeFrom(entity)), compareTo);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.RESOURCE;
	}

}
