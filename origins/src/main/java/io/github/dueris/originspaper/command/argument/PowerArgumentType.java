package io.github.dueris.originspaper.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.registry.Registries;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class PowerArgumentType implements CustomArgumentType<PowerType, NamespacedKey> {
	public static final DynamicCommandExceptionType POWER_NOT_FOUND = new DynamicCommandExceptionType((o) -> {
		return Component.literal("Power not found");
	});

	public static @NotNull PowerArgumentType power() {
		return new PowerArgumentType();
	}

	public static PowerType getPower(@NotNull CommandContext<CommandSourceStack> context, String argumentName) {
		return context.getArgument(argumentName, PowerType.class);
	}

	public PowerType parse(StringReader reader) throws CommandSyntaxException {
		ResourceLocation id = ResourceLocation.readNonEmpty(reader);
		return OriginsPaper.getRegistry().retrieve(Registries.POWER).getOptional(id).orElseThrow(() -> {
			return POWER_NOT_FOUND.create(id);
		});
	}

	@Override
	public @NotNull ArgumentType<NamespacedKey> getNativeType() {
		return ArgumentTypes.namespacedKey();
	}

	@Override
	public @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext context, @NotNull SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(OriginsPaper.getRegistry().retrieve(Registries.POWER).keySet().stream(), builder);
	}
}
