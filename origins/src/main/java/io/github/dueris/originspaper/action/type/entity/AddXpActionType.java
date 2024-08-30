package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class AddXpActionType {

	public static void action(Entity entity, int points, int levels) {

		if (!(entity instanceof Player player)) {
			return;
		}

		player.giveExperiencePoints(points);
		player.giveExperienceLevels(levels);

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("add_xp"),
			new SerializableData()
				.add("points", SerializableDataTypes.INT, 0)
				.add("levels", SerializableDataTypes.INT, 0),
			(data, entity) -> action(entity,
				data.get("points"),
				data.get("levels")
			)
		);
	}

}
