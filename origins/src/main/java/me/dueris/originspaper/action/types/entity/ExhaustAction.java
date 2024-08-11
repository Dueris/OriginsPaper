package me.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ExhaustAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("exhaust"),
			InstanceDefiner.instanceDefiner()
				.add("amount", SerializableDataTypes.FLOAT),
			(data, entity) -> {
				if (entity instanceof Player)
					((Player) entity).getFoodData().addExhaustion(data.getFloat("amount"));
			}
		);
	}
}
