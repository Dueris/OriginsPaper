package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SetOnFireActionType {

	public static void action(@NotNull Entity entity, float seconds) {
		entity.igniteForSeconds(seconds);
	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("set_on_fire"),
			new SerializableData()
				.add("duration", SerializableDataTypes.FLOAT),
			(data, entity) -> action(entity,
				data.get("duration")
			)
		);
	}

}
