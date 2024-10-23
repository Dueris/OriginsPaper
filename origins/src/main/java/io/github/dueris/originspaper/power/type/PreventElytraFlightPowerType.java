package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class PreventElytraFlightPowerType extends PowerType {

	private final Consumer<Entity> entityAction;

	public PreventElytraFlightPowerType(Power power, LivingEntity entity, Consumer<Entity> entityAction) {
		super(power, entity);
		this.entityAction = entityAction;
		setTicking();
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("prevent_elytra_flight"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null),
			data -> (power, entity) -> new PreventElytraFlightPowerType(power, entity,
				data.get("entity_action")
			)
		).allowCondition();

	}

	@Override
	public void tick() {
		this.entity.setSharedFlag(7, false);
	}

	public void executeAction(Entity entity) {

		if (entityAction != null) {
			entityAction.accept(entity);
		}

	}

}
