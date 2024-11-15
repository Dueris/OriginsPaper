package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DimensionEntityConditionType extends EntityConditionType {

	public static final TypedDataObjectFactory<DimensionEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("dimension", SerializableDataTypes.DIMENSION),
		data -> new DimensionEntityConditionType(
			data.get("dimension")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("dimension", conditionType.dimension)
	);

	private final ResourceKey<Level> dimension;

	public DimensionEntityConditionType(ResourceKey<Level> dimension) {
		this.dimension = dimension;
	}

	@Override
	public boolean test(Entity entity) {
		return entity.level().dimension().equals(dimension);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.DIMENSION;
	}

}
