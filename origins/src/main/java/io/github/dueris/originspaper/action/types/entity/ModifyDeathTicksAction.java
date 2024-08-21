package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ModifyDeathTicksAction {

	public static void action(SerializableData.Instance data, Entity entity) {
		if (entity instanceof LivingEntity living) {
			living.deathTime = (int) data.<Modifier>get("modifier").apply(entity, living.deathTime);
		}
	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("modify_death_ticks"),
			SerializableData.serializableData()
				.add("modifier", Modifier.DATA_TYPE),
			ModifyDeathTicksAction::action
		);
	}
}
