package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ExecuteCommandEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<ExecuteCommandEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("command", SerializableDataTypes.STRING),
		data -> new ExecuteCommandEntityActionType(
			data.get("command")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("command", actionType.command)
	);

	private final String command;

	public ExecuteCommandEntityActionType(String command) {
		this.command = command;
	}

	@Override
	protected void execute(Entity entity) {

		if (!(entity.level() instanceof ServerLevel serverWorld)) {
			return;
		}

		MinecraftServer server = serverWorld.getServer();
		CommandSourceStack commandSource = entity.createCommandSourceStack()
			.withPermission(OriginsPaper.config.executeCommand.permissionLevel)
			.withSource(CommandSource.NULL);

		if (OriginsPaper.config.executeCommand.showOutput) {

			CommandSource output = entity instanceof ServerPlayer serverPlayer && serverPlayer.connection != null
				? serverPlayer
				: server;

			commandSource = commandSource.withSource(output);

		}

		server.getCommands().performPrefixedCommand(commandSource, command);

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.EXECUTE_COMMAND;
	}

}
