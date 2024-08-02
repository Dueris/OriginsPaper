package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SetOnFireAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("set_on_fire"),
			InstanceDefiner.instanceDefiner()
				.add("duration", SerializableDataTypes.INT),
			(data, entity) -> entity.igniteForSeconds(data.getInt("duration")));
	}
}
