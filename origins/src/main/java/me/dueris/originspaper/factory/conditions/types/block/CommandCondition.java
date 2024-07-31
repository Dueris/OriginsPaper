package me.dueris.originspaper.factory.conditions.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.factory.data.types.Comparison;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandCondition {

	public static boolean condition(DeserializedFactoryJson data, BlockInWorld cachedBlockPosition) {

		MinecraftServer server = ((Level) cachedBlockPosition.getLevel()).getServer();
		if (server == null) {
			return false;
		}

		AtomicInteger result = new AtomicInteger();
		CommandSourceStack source = server.createCommandSourceStack()
			.withPosition(cachedBlockPosition.getPos().getCenter())
			.withPermission(4)
			.withCallback((successful, returnValue) -> result.set(returnValue))
			.withSuppressedOutput();

		Comparison comparison = data.get("comparison");
		String command = data.get("command");

		int compareTo = data.get("compare_to");
		server.getCommands().performPrefixedCommand(source, command);

		return comparison.compare(result.get(), compareTo);

	}

	public static ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("command"),
			InstanceDefiner.instanceDefiner()
				.add("command", SerializableDataTypes.STRING)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
				.add("compare_to", SerializableDataTypes.INT, 1),
			CommandCondition::condition
		);
	}
}
