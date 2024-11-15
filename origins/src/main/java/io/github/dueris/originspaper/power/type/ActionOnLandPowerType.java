package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionOnLandPowerType extends PowerType {

	public static final TypedDataObjectFactory<ActionOnLandPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE),
		(data, condition) -> new ActionOnLandPowerType(
			data.get("entity_action"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
	);

	private final EntityAction entityAction;

	public ActionOnLandPowerType(EntityAction entityAction, Optional<EntityCondition> condition) {
		super(condition);
		this.entityAction = entityAction;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ACTION_ON_LAND;
	}

	public void executeAction() {
		entityAction.execute(getHolder());
	}

}
