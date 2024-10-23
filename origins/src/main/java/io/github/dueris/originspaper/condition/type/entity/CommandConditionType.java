package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandConditionType {

	public static boolean condition(Entity entity, String command, Comparison comparison, int compareTo) {

		MinecraftServer server = entity.getServer();
		AtomicInteger result = new AtomicInteger();

		if (server == null) {
			return false;
		}

		CommandSourceStack source = entity.createCommandSourceStack()
			.withSource(OriginsPaper.showCommandOutput ? entity : CommandSource.NULL)
			.withPermission(4)
			.withCallback((successful, returnValue) -> result.set(returnValue));

		server.getCommands().performPrefixedCommand(source, command);
		return comparison.compare(result.get(), compareTo);

	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("command"),
			new SerializableData()
				.add("command", SerializableDataTypes.STRING)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			(data, entity) -> condition(entity,
				data.get("command"),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
