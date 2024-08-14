package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.power.ResourcePower;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SetResourceAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("set_resource"),
			InstanceDefiner.instanceDefiner()
				.add("resource", SerializableDataTypes.IDENTIFIER)
				.add("value", SerializableDataTypes.INT),
			(data, entity) -> {
				Optional<ResourcePower.Bar> resourceBar = ResourcePower.getDisplayedBar(entity, data.getId("resource").toString());
				resourceBar.ifPresent((bar) -> {
					int val = data.get("value");
					bar.change(val, "set");
				});
			}
		);
	}
}
