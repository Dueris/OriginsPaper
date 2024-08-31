package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerReference;
import io.github.dueris.originspaper.power.type.CooldownPower;
import io.github.dueris.originspaper.power.type.ResourcePower;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SetResourceActionType {

	public static void action(Entity entity, PowerReference power, int value) {
		switch (power.getType()) {
			case ResourcePower varInt -> {
				varInt.setValue(entity, value);
			}
			case CooldownPower cooldown -> {
				cooldown.setCooldown(entity, value);
			}
			case null, default -> {

			}
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("set_resource"),
			new SerializableData()
				.add("resource", ApoliDataTypes.POWER_REFERENCE)
				.add("value", SerializableDataTypes.INT),
			(data, entity) -> action(entity,
				data.get("resource"),
				data.get("value")
			)
		);
	}

}
