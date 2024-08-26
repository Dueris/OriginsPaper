package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class HealAction {

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("heal"),
			SerializableData.serializableData()
				.add("amount", SerializableDataTypes.FLOAT),
			(data, entity) -> {
				if (entity instanceof LivingEntity) {
					((LivingEntity) entity).heal(data.getFloat("amount"));
				}
			}
		);
	}
}
