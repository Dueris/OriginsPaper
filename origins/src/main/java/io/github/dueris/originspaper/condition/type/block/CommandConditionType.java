package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandConditionType {

	public static boolean condition(BlockInWorld cachedBlock, String command, Comparison comparison, int compareTo) {

		MinecraftServer server = ((Level) cachedBlock.getLevel()).getServer();
		if (server == null) {
			return false;
		}

		AtomicInteger result = new AtomicInteger();
		CommandSourceStack source = server.createCommandSourceStack()
			.withSource(OriginsPaper.config.executeCommand.showOutput ? server : CommandSource.NULL)
			.withPosition(cachedBlock.getPos().getCenter())
			.withPermission(OriginsPaper.config.executeCommand.permissionLevel)
			.withCallback((successful, returnValue) -> result.set(returnValue));

		server.getCommands().performPrefixedCommand(source, command);
		return comparison.compare(result.get(), compareTo);

	}

	public static ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("command"),
			new SerializableData()
				.add("command", SerializableDataTypes.STRING)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
				.add("compare_to", SerializableDataTypes.INT, 1),
			(data, cachedBlock) -> condition(cachedBlock,
				data.get("command"),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
