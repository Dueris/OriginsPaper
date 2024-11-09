package io.github.dueris.originspaper.power.type.origins;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.power.type.ActionOnCallbackPowerType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class OriginsCallbackPowerType extends ActionOnCallbackPowerType {

	private final Consumer<Entity> entityActionChosen;
	private final boolean executeChosenWhenOrb;

	public OriginsCallbackPowerType(Power power, LivingEntity entity, Consumer<Entity> entityActionRespawned, Consumer<Entity> entityActionRemoved, Consumer<Entity> entityActionGained, Consumer<Entity> entityActionLost, Consumer<Entity> entityActionAdded, Consumer<Entity> entityActionChosen, boolean executeChosenWhenOrb) {
		super(power, entity, entityActionRespawned, entityActionRemoved, entityActionGained, entityActionLost, entityActionAdded);
		this.entityActionChosen = entityActionChosen;
		this.executeChosenWhenOrb = executeChosenWhenOrb;
	}

	public void onChosen(boolean isOrbOfOrigins) {

		if (this.isActive() && entityActionChosen != null && (!isOrbOfOrigins || executeChosenWhenOrb)) {
			entityActionChosen.accept(entity);
		}

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.originIdentifier("action_on_callback"),
			new SerializableData()
				.add("entity_action_respawned", ApoliDataTypes.ENTITY_ACTION, null)
				.add("entity_action_removed", ApoliDataTypes.ENTITY_ACTION, null)
				.add("entity_action_gained", ApoliDataTypes.ENTITY_ACTION, null)
				.add("entity_action_lost", ApoliDataTypes.ENTITY_ACTION, null)
				.add("entity_action_added", ApoliDataTypes.ENTITY_ACTION, null)
				.add("entity_action_chosen", ApoliDataTypes.ENTITY_ACTION, null)
				.add("execute_chosen_when_orb", SerializableDataTypes.BOOLEAN, true),
			data -> (power, livingEntity) -> new OriginsCallbackPowerType(power, livingEntity,
				data.get("entity_action_respawned"),
				data.get("entity_action_removed"),
				data.get("entity_action_gained"),
				data.get("entity_action_lost"),
				data.get("entity_action_added"),
				data.get("entity_action_chosen"),
				data.get("execute_chosen_when_orb")
			)
		).allowCondition();
	}

}
