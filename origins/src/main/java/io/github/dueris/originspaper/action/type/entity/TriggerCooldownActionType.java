package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.type.CooldownPower;
import io.github.dueris.originspaper.power.factory.PowerReference;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class TriggerCooldownActionType {

	public static void action(Entity entity, @NotNull PowerReference power) {

		if (power.getType() instanceof CooldownPower cooldown) {
			cooldown.use(entity);
		}

	}

	@Contract(" -> new")
	public static @NotNull ActionTypeFactory<Entity> getFactory() {
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
