package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.ResourceOperation;
import io.github.dueris.originspaper.power.ResourcePower;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

public class ChangeResourceAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("change_resource"),
			InstanceDefiner.instanceDefiner()
				.add("resource", SerializableDataTypes.IDENTIFIER)
				.add("change", SerializableDataTypes.INT)
				.add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD),
			(data, entity) -> {
				Optional<ResourcePower.Bar> resourceBar = ResourcePower.getDisplayedBar(entity, data.getId("resource").toString());
				resourceBar.ifPresent((bar) -> {
					int change = data.get("change");
					ResourceOperation operation = data.get("operation");
					bar.change(change, operation.toString().toLowerCase(Locale.ROOT));
				});
			}
		);
	}
}
