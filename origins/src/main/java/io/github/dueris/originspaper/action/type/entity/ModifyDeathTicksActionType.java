package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ModifyDeathTicksActionType {

	public static void action(Entity entity, Modifier modifier) {

		if (entity instanceof LivingEntity living) {
			living.deathTime = (int) modifier.apply(entity, living.deathTime);
		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("modify_death_ticks"),
			new SerializableData()
				.add("modifier", Modifier.DATA_TYPE),
			(data, entity) -> action(entity,
				data.get("modifier")
			)
		);
	}

}
