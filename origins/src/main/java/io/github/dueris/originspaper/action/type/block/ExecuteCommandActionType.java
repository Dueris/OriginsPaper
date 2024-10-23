package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

public class ExecuteCommandActionType {

	public static void action(Level world, BlockPos pos, String command) {

		MinecraftServer server = world.getServer();
		if (server == null) {
			return;
		}

		CommandSourceStack source = server.createCommandSourceStack()
			.withSource(OriginsPaper.showCommandOutput ? server : CommandSource.NULL)
			.withPosition(pos.getCenter())
			.withPermission(4);

		server.getCommands().performPrefixedCommand(source, command);

	}

	public static ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("execute_command"),
			new SerializableData()
				.add("command", SerializableDataTypes.STRING),
			(data, block) -> action(block.getLeft(), block.getMiddle(),
				data.get("command")
			)
		);
	}

}
