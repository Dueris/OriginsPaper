package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class ActionOnLandPowerType extends PowerType {

	private final Consumer<Entity> entityAction;

	public ActionOnLandPowerType(Power power, LivingEntity entity, Consumer<Entity> entityAction) {
		super(power, entity);
		this.entityAction = entityAction;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("action_on_land"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null),
			data -> (power, entity) -> new ActionOnLandPowerType(power, entity,
				data.get("entity_action")
			)
		).allowCondition();
	}

	public void executeAction() {
		entityAction.accept(entity);
	}

}

