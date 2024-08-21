package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ToggleAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("toggle"),
			SerializableData.serializableData()
				.add("power", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				if (entity instanceof LivingEntity) {
				}
			}
		);
	}
}
