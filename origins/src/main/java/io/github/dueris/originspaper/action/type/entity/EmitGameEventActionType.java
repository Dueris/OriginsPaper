package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;

public class EmitGameEventActionType {

	public static void action(Entity entity, Holder<GameEvent> gameEvent) {
		entity.gameEvent(gameEvent);
	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("emit_game_event"),
			new SerializableData()
				.add("event", SerializableDataTypes.GAME_EVENT_ENTRY),
			(data, entity) -> action(entity,
				data.get("event")
			)
		);
	}

}
