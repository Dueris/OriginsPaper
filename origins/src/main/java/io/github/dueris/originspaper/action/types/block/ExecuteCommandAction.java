package io.github.dueris.originspaper.action.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

public class ExecuteCommandAction {

	public static @NotNull ActionFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionFactory<>(
				OriginsPaper.apoliIdentifier("execute_command"),
				SerializableData.serializableData()
						.add("command", SerializableDataTypes.STRING),
				(data, block) -> {
					MinecraftServer server = block.getLeft().getServer();
					if (server != null) {
						String blockName = block.getLeft().getBlockState(block.getMiddle()).getBlock().getDescriptionId();
						CommandSourceStack source = new CommandSourceStack(
								OriginsPaper.showCommandOutput ? server : CommandSource.NULL,
								new Vec3(block.getMiddle().getX() + 0.5, block.getMiddle().getY() + 0.5, block.getMiddle().getZ() + 0.5),
								new Vec2(0, 0),
								(ServerLevel) block.getLeft(),
								4,
								blockName,
								Component.translatable(blockName),
								server,
								null);
						server.getCommands().performPrefixedCommand(source, data.getString("command"));
					}
				}
		);
	}
}
