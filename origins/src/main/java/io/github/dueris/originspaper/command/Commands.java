package io.github.dueris.originspaper.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public class Commands {
	public static void bootstrap(CommandDispatcher<CommandSourceStack> dispatcher) {
		OriginCommand.register(dispatcher);
		PowerCommand.register(dispatcher);
		ResourceCommand.register(dispatcher);
	}

	public static void unload(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.getRoot().removeCommand("origin");
		dispatcher.getRoot().removeCommand("power");
		dispatcher.getRoot().removeCommand("resource");
	}
}
