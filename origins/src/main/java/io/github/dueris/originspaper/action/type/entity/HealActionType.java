package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HealActionType {

	public static void action(Entity entity, float amount) {

		if (entity instanceof LivingEntity living) {
			living.heal(amount);
		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("heal"),
			new SerializableData()
				.add("amount", SerializableDataTypes.FLOAT),
			(data, entity) -> action(entity,
				data.get("amount")
			)
		);
	}

}
