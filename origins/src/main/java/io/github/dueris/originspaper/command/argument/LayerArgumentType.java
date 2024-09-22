package io.github.dueris.originspaper.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.util.LangFile;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class LayerArgumentType implements CustomArgumentType<OriginLayer, NamespacedKey> {

	public static final DynamicCommandExceptionType LAYER_NOT_FOUND = new DynamicCommandExceptionType(
		o -> LangFile.translatable("commands.origin.layer_not_found", o)
	);

	public static @NotNull LayerArgumentType layer() {
		return new LayerArgumentType();
	}

	public static OriginLayer getLayer(@NotNull CommandContext<CommandSourceStack> context, String argumentName) throws CommandSyntaxException {

		try {
			return context.getArgument(argumentName, OriginLayer.class);
		} catch (IllegalArgumentException e) {
			throw LAYER_NOT_FOUND.create(null);
		}

	}

	@Override
	public OriginLayer parse(StringReader stringReader) throws CommandSyntaxException {
		return OriginsPaper.getLayer(ResourceLocation.read(stringReader));
	}

	@Override
	public @NotNull ArgumentType<NamespacedKey> getNativeType() {
		return ArgumentTypes.namespacedKey();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(OriginLayer.REGISTRY.values().stream().filter(OriginLayer::isEnabled).map(OriginLayer::getId), builder);
	}

}
