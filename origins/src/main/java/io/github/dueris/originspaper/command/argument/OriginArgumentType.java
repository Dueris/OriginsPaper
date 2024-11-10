package io.github.dueris.originspaper.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.origin.OriginLayerManager;
import io.github.dueris.originspaper.origin.OriginManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class OriginArgumentType implements CustomArgumentType<Origin, NamespacedKey> {

	public static final DynamicCommandExceptionType ORIGIN_NOT_FOUND = new DynamicCommandExceptionType(
		o -> Component.translatable("commands.origin.origin_not_found", o)
	);

	public static OriginArgumentType origin() {
		return new OriginArgumentType();
	}

	public static Origin getOrigin(CommandContext<CommandSourceStack> context, String argumentName) {
		return context.getArgument(argumentName, Origin.class);
	}

	@Override
	public @NotNull Origin parse(@NotNull StringReader reader) throws CommandSyntaxException {
		ResourceLocation id = ResourceLocation.readNonEmpty(reader);
		return OriginManager
			.getOptional(id)
			.orElseThrow(() -> ORIGIN_NOT_FOUND.create(id));
	}

	@Override
	public @NotNull ArgumentType<NamespacedKey> getNativeType() {
		return ArgumentTypes.namespacedKey();
	}

	@Override
	public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
		String input = context.getInput();

		String[] args = input.split(" ");

		if (args.length > 3) {
			String layerId = args[3];

			OriginLayer layer = OriginLayerManager.get(ResourceLocation.read(layerId).getOrThrow());

			if (layer != null) {
				return SharedSuggestionProvider.suggestResource(layer.getOrigins(), builder);
			}
		}

		return Suggestions.empty();
	}

}
