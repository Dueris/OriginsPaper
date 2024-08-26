package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ExtinguishAction {

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("extinguish"),
			SerializableData.serializableData(),
			(data, entity) -> entity.clearFire());
	}
}
