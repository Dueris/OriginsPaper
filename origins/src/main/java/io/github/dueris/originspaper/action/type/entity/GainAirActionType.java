package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class GainAirActionType {

	public static void action(Entity entity, int value) {

		if (entity instanceof LivingEntity living) {
			living.setAirSupply(Math.min(living.getAirSupply() + value, living.getMaxAirSupply()));
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("gain_air"),
			new SerializableData()
				.add("value", SerializableDataTypes.INT),
			(data, entity) -> action(entity,
				data.get("value")
			)
		);
	}

}
