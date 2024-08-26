package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Entity entity) {

		MinecraftServer server = entity.getServer();
		AtomicInteger result = new AtomicInteger();

		if (server == null) {
			return false;
		}

		CommandSource commandOutput = OriginsPaper.showCommandOutput && (!(entity instanceof ServerPlayer player) || player.connection != null)
			? entity
			: CommandSource.NULL;
		CommandSourceStack source = entity.createCommandSourceStack()
			.withSource(commandOutput)
			.withPermission(4)
			.withCallback((successful, returnValue) -> result.set(returnValue));

		Comparison comparison = data.get("comparison");
		String command = data.get("command");

		int compareTo = data.get("compare_to");
		server.getCommands().performPrefixedCommand(source, command);

		return comparison.compare(result.get(), compareTo);

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("command"),
			SerializableData.serializableData()
				.add("command", SerializableDataTypes.STRING)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			CommandCondition::condition
		);
	}
}
