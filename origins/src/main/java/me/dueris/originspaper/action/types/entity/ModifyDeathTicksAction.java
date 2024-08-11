package me.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.data.types.modifier.Modifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ModifyDeathTicksAction {

	public static void action(DeserializedFactoryJson data, Entity entity) {
		if (entity instanceof LivingEntity living) {
			living.deathTime = (int) data.<Modifier>get("modifier").apply(entity, living.deathTime);
		}
	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("modify_death_ticks"),
			InstanceDefiner.instanceDefiner()
				.add("modifier", Modifier.DATA_TYPE),
			ModifyDeathTicksAction::action
		);
	}
}
