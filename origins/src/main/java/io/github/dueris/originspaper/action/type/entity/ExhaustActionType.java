package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class ExhaustActionType {

	public static void action(Entity entity, float amount) {

		if (entity instanceof Player player) {
			player.causeFoodExhaustion(amount);
		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("exhaust"),
			new SerializableData()
				.add("amount", SerializableDataTypes.FLOAT),
			(data, entity) -> action(entity,
				data.get("amount")
			)
		);
	}

}
