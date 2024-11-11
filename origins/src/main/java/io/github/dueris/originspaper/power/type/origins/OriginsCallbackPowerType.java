package io.github.dueris.originspaper.power.type.origins;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.power.type.ActionOnCallbackPowerType;
import io.github.dueris.originspaper.power.type.PowerType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class OriginsCallbackPowerType extends ActionOnCallbackPowerType {

	public static final TypedDataObjectFactory<OriginsCallbackPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action_respawned", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("entity_action_removed", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("entity_action_gained", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("entity_action_lost", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("entity_action_added", EntityAction.DATA_TYPE.optional(), Optional.empty()),
		(data, condition) -> new OriginsCallbackPowerType(
			data.get("entity_action_respawned"),
			data.get("entity_action_removed"),
			data.get("entity_action_gained"),
			data.get("entity_action_lost"),
			data.get("entity_action_added"),
			data.get("entity_action_chosen"),
			data.getBoolean("execute_chosen_when_orb"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action_respawned", powerType.entityActionRespawned)
			.set("entity_action_removed", powerType.entityActionRemoved)
			.set("entity_action_gained", powerType.entityActionGained)
			.set("entity_action_lost", powerType.entityActionLost)
			.set("entity_action_added", powerType.entityActionAdded)
			.set("entity_action_chosen", powerType.entityActionChosen)
			.set("execute_chosen_when_orb", powerType.executeChosenWhenOrb)
	);

	private final Optional<EntityAction> entityActionChosen;
	private final boolean executeChosenWhenOrb;

	public OriginsCallbackPowerType(Optional<EntityAction> entityActionRespawned, Optional<EntityAction> entityActionRemoved, Optional<EntityAction> entityActionGained, Optional<EntityAction> entityActionLost, Optional<EntityAction> entityActionAdded, Optional<EntityAction> entityActionChosen, boolean executeChosenWhenOrb, Optional<EntityCondition> condition) {
		super(entityActionRespawned, entityActionRemoved, entityActionGained, entityActionLost, entityActionAdded, condition);
		this.entityActionChosen = entityActionChosen;
		this.executeChosenWhenOrb = executeChosenWhenOrb;
	}

	public void onChosen(boolean isOrbOfOrigins) {

		if (this.isActive() && entityActionChosen.isPresent() && (!isOrbOfOrigins || executeChosenWhenOrb)) {
			entityActionChosen.get().execute(getHolder());
		}

	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return OriginsPowerTypes.ACTION_ON_CALLBACK;
	}
}
