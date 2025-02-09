package io.github.dueris.originspaper.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.origin.OriginLayerManager;
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
public class LayerArgumentType implements CustomArgumentType<OriginLayer, NamespacedKey> {

	public static final DynamicCommandExceptionType LAYER_NOT_FOUND = new DynamicCommandExceptionType(
		o -> Component.translatable("commands.origin.layer_not_found", o)
	);

	public static @NotNull LayerArgumentType layer() {
		return new LayerArgumentType();
	}

	public static OriginLayer getLayer(@NotNull CommandContext<CommandSourceStack> context, String argumentName) {
		return context.getArgument(argumentName, OriginLayer.class);
	}

	@Override
	public @NotNull OriginLayer parse(@NotNull StringReader reader) throws CommandSyntaxException {
		ResourceLocation id = ResourceLocation.readNonEmpty(reader);
		return OriginLayerManager.getResult(id)
			.result()
			.orElseThrow(() -> LAYER_NOT_FOUND.create(id));
	}

	@Override
	public @NotNull ArgumentType<NamespacedKey> getNativeType() {
		return ArgumentTypes.namespacedKey();
	}

	@Override
	public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(OriginLayerManager.values().stream().filter(OriginLayer::isEnabled).map(OriginLayer::getId), builder);
	}

}
