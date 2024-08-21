package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EmitGameEvent {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("emit_game_event"),
			SerializableData.serializableData()
				.add("event", SerializableDataTypes.GAME_EVENT),
			(data, entity) -> entity.gameEvent(data.get("event")));
	}
}
