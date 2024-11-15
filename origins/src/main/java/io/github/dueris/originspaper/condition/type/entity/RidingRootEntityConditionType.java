package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RidingRootEntityConditionType extends EntityConditionType {

	public static final TypedDataObjectFactory<RidingRootEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty()),
		data -> new RidingRootEntityConditionType(
			data.get("bientity_condition")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("bientity_condition", conditionType.biEntityCondition)
	);

	private final Optional<BiEntityCondition> biEntityCondition;

	public RidingRootEntityConditionType(Optional<BiEntityCondition> biEntityCondition) {
		this.biEntityCondition = biEntityCondition;
	}

	@Override
	public boolean test(Entity entity) {
		Entity rootVehicle = entity.getRootVehicle();
		return rootVehicle != null
			&& biEntityCondition.map(condition -> condition.test(entity, rootVehicle)).orElse(true);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.RIDING_ROOT;
	}

}
