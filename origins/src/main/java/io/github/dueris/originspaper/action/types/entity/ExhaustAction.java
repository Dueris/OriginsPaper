package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ExhaustAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("exhaust"),
				SerializableData.serializableData()
						.add("amount", SerializableDataTypes.FLOAT),
				(data, entity) -> {
					if (entity instanceof Player)
						((Player) entity).getFoodData().addExhaustion(data.getFloat("amount"));
				}
		);
	}
}
