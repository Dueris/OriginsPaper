package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class ActionOnCallbackPowerType extends PowerType {

	public static final TypedDataObjectFactory<ActionOnCallbackPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action_respawned", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("entity_action_removed", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("entity_action_gained", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("entity_action_lost", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("entity_action_added", EntityAction.DATA_TYPE.optional(), Optional.empty()),
		(data, condition) -> new ActionOnCallbackPowerType(
			data.get("entity_action_respawned"),
			data.get("entity_action_removed"),
			data.get("entity_action_gained"),
			data.get("entity_action_lost"),
			data.get("entity_action_added"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action_respawned", powerType.entityActionRespawned)
			.set("entity_action_removed", powerType.entityActionRemoved)
			.set("entity_action_gained", powerType.entityActionGained)
			.set("entity_action_lost", powerType.entityActionLost)
			.set("entity_action_added", powerType.entityActionAdded)
	);

	protected final Optional<EntityAction> entityActionRespawned;
	protected final Optional<EntityAction> entityActionRemoved;
	protected final Optional<EntityAction> entityActionGained;
	protected final Optional<EntityAction> entityActionLost;
	protected final Optional<EntityAction> entityActionAdded;

	public ActionOnCallbackPowerType(Optional<EntityAction> entityActionRespawned, Optional<EntityAction> entityActionRemoved, Optional<EntityAction> entityActionGained, Optional<EntityAction> entityActionLost, Optional<EntityAction> entityActionAdded, Optional<EntityCondition> condition) {
		super(condition);
		this.entityActionRespawned = entityActionRespawned;
		this.entityActionRemoved = entityActionRemoved;
		this.entityActionGained = entityActionGained;
		this.entityActionLost = entityActionLost;
		this.entityActionAdded = entityActionAdded;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ACTION_ON_CALLBACK;
	}

	@Override
	public void onRespawn() {
		entityActionRespawned
			.filter(action -> this.isActive())
			.ifPresent(action -> action.execute(getHolder()));
	}

	@Override
	public void onGained() {
		entityActionGained
			.filter(action -> this.isActive())
			.ifPresent(action -> action.execute(getHolder()));
	}

	@Override
	public void onRemoved() {
		entityActionRemoved
			.filter(action -> this.isActive())
			.ifPresent(action -> action.execute(getHolder()));
	}

	@Override
	public void onLost() {
		entityActionLost
			.filter(action -> this.isActive())
			.ifPresent(action -> action.execute(getHolder()));
	}

	@Override
	public void onAdded() {
		entityActionAdded
			.filter(action -> this.isActive())
			.ifPresent(action -> action.execute(getHolder()));
	}

}
