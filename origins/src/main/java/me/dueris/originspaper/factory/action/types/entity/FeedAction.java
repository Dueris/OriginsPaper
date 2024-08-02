package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FeedAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("feed"),
			InstanceDefiner.instanceDefiner()
				.add("food", SerializableDataTypes.INT)
				.add("saturation", SerializableDataTypes.FLOAT),
			(data, entity) -> {
				if (entity instanceof Player) {
					((Player) entity).getFoodData().eat(data.getInt("food"), data.getFloat("saturation"));
				}
			}
		);
	}
}
