package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.power.type.CooldownPowerType;
import net.minecraft.world.entity.Entity;

public class TriggerCooldownActionType {

	public static void action(Entity entity, PowerReference power) {

		if (power.getType(entity) instanceof CooldownPowerType cooldown) {
			cooldown.use();
		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("trigger_cooldown"),
			new SerializableData()
				.add("power", ApoliDataTypes.POWER_REFERENCE),
			(data, entity) -> action(entity,
				data.get("power")
			)
		);
	}

}
