package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EmitGameEvent {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("emit_game_event"),
			InstanceDefiner.instanceDefiner()
				.add("event", SerializableDataTypes.GAME_EVENT),
			(data, entity) -> entity.gameEvent(data.get("event")));
	}
}
