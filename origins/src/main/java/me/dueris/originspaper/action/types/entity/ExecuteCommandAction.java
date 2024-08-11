package me.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ExecuteCommandAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("execute_command"),
			InstanceDefiner.instanceDefiner()
				.add("command", SerializableDataTypes.STRING),
			(data, entity) -> {
				MinecraftServer server = entity.level().getServer();
				if (server != null) {
					boolean validOutput = !(entity instanceof ServerPlayer) || ((ServerPlayer) entity).connection != null;
					CommandSourceStack source = new CommandSourceStack(
						OriginsPaper.showCommandOutput && validOutput ? entity : CommandSource.NULL,
						entity.position(),
						entity.getRotationVector(),
						entity.level() instanceof ServerLevel ? (ServerLevel) entity.level() : null,
						4,
						entity.getName().getString(),
						entity.getDisplayName(),
						entity.level().getServer(),
						entity);
					String cmd = data.getString("command");
					// Fix the command to support our pehuki implementation
					if (cmd.contains("scale")) {
						if (cmd.contains("@s")) {
							cmd = cmd.replace(" @s", "");
						}
					}
					server.getCommands().performPrefixedCommand(source, cmd);
				}
			}
		);
	}
}
