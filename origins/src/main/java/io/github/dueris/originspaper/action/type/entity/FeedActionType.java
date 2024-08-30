package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FeedActionType {

	public static void action(Entity entity, int nutrition, float saturation) {

		if (entity instanceof Player player) {
			player.getFoodData().eat(nutrition, saturation);
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("feed"),
			new SerializableData()
				.add("nutrition", SerializableDataTypes.INT)
				.add("saturation", SerializableDataTypes.FLOAT),
			(data, entity) -> action(entity,
				data.get("nutrition"),
				data.get("saturation")
			)
		);
	}

}
