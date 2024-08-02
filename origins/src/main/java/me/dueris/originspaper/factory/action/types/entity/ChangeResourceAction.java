package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.factory.data.types.ResourceOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ChangeResourceAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("change_resource"),
			InstanceDefiner.instanceDefiner()
				.add("resource", SerializableDataTypes.IDENTIFIER)
				.add("change", SerializableDataTypes.INT)
				.add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD),
			(data, entity) -> {
				if (entity instanceof Player player) {
					// TODO
				}
			}
		);
	}
}
