package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.power.ResourcePower;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModifyResourceAction {

	public static void action(SerializableData.@NotNull Instance data, Entity entity) {
		Optional<ResourcePower.Bar> resourceBar = ResourcePower.getDisplayedBar(entity, data.getString("resource"));
		resourceBar.ifPresent((bar) -> {
			Modifier modifier = data.get("modifier");
			Long change = Math.round(modifier.apply(entity, bar.getMappedProgress()));
			bar.change(change.intValue(), "set");
		});
	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("modify_resource"),
			SerializableData.serializableData()
				.add("modifier", Modifier.DATA_TYPE)
				.add("resource", SerializableDataTypes.IDENTIFIER),
			ModifyResourceAction::action
		);
	}
}
