package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ExecuteCommandActionType {

	public static void action(@NotNull Entity entity, String command) {

		MinecraftServer server = entity.getServer();
		if (server == null) {
			return;
		}

		CommandSourceStack source = entity.createCommandSourceStack()
			.withSource(OriginsPaper.showCommandOutput ? entity : CommandSource.NULL)
			.withPermission(4);

		server.getCommands().performPrefixedCommand(source, command);

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("execute_command"),
			new SerializableData()
				.add("command", SerializableDataTypes.STRING),
			(data, entity) -> action(entity,
				data.get("command")
			)
		);
	}

}
