package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class ActionOnCallbackPowerType extends PowerType {

	private final Consumer<Entity> entityActionRespawned;
	private final Consumer<Entity> entityActionRemoved;
	private final Consumer<Entity> entityActionGained;
	private final Consumer<Entity> entityActionLost;
	private final Consumer<Entity> entityActionAdded;

	public ActionOnCallbackPowerType(Power power, LivingEntity entity, Consumer<Entity> entityActionRespawned, Consumer<Entity> entityActionRemoved, Consumer<Entity> entityActionGained, Consumer<Entity> entityActionLost, Consumer<Entity> entityActionAdded) {
		super(power, entity);
		this.entityActionRespawned = entityActionRespawned;
		this.entityActionRemoved = entityActionRemoved;
		this.entityActionGained = entityActionGained;
		this.entityActionLost = entityActionLost;
		this.entityActionAdded = entityActionAdded;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("action_on_callback"),
			new SerializableData()
				.add("entity_action_respawned", ApoliDataTypes.ENTITY_ACTION, null)
				.add("entity_action_removed", ApoliDataTypes.ENTITY_ACTION, null)
				.add("entity_action_gained", ApoliDataTypes.ENTITY_ACTION, null)
				.add("entity_action_lost", ApoliDataTypes.ENTITY_ACTION, null)
				.add("entity_action_added", ApoliDataTypes.ENTITY_ACTION, null),
			data -> (power, entity) -> new ActionOnCallbackPowerType(power, entity,
				data.get("entity_action_respawned"),
				data.get("entity_action_removed"),
				data.get("entity_action_gained"),
				data.get("entity_action_lost"),
				data.get("entity_action_added")
			)
		).allowCondition();
	}

	@Override
	public void onRespawn() {

		if (this.isActive() && entityActionRespawned != null) {
			entityActionRespawned.accept(entity);
		}

	}

	@Override
	public void onGained() {

		if (this.isActive() && entityActionGained != null) {
			entityActionGained.accept(entity);
		}

	}

	@Override
	public void onRemoved() {

		if (this.isActive() && entityActionRemoved != null) {
			entityActionRemoved.accept(entity);
		}

	}

	@Override
	public void onLost() {

		if (this.isActive() && entityActionLost != null) {
			entityActionLost.accept(entity);
		}

	}

	@Override
	public void onAdded() {

		if (this.isActive() && entityActionAdded != null) {
			entityActionAdded.accept(entity);
		}

	}

}
