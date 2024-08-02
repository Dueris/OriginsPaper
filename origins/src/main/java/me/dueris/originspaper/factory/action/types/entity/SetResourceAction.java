package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SetResourceAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("set_resource"),
			InstanceDefiner.instanceDefiner()
				.add("resource", SerializableDataTypes.IDENTIFIER)
				.add("value", SerializableDataTypes.INT),
			(data, entity) -> {
				// TODO
//				Optional<Resource.Bar> resourceBar = Resource.getDisplayedBar(entity, data.getString("resource"));
//				resourceBar.ifPresent((bar) -> {
//					int val = data.getNumber("value").getInt();
//					bar.change(val, "set");
//				});
			}
		);
	}
}
