package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ClearEffectAction {

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("clear_effect"),
			SerializableData.serializableData()
				.add("effect", SerializableDataTypes.STATUS_EFFECT_ENTRY, null),
			(data, entity) -> {
				if (entity instanceof LivingEntity le) {
					if (data.isPresent("effect")) {
						le.removeEffect(data.get("effect"));
					} else {
						le.removeAllEffects();
					}
				}
			}
		);
	}
}
