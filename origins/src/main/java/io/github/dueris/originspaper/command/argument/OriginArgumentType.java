package io.github.dueris.originspaper.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.util.LangFile;
import io.github.dueris.originspaper.util.Util;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OriginArgumentType implements CustomArgumentType<Origin, NamespacedKey> {

	public static final DynamicCommandExceptionType ORIGIN_NOT_FOUND = new DynamicCommandExceptionType(
		o -> LangFile.translatable("commands.origin.origin_not_found", o)
	);

	public static @NotNull OriginArgumentType origin() {
		return new OriginArgumentType();
	}

	public static Origin getOrigin(@NotNull CommandContext<CommandSourceStack> context, String argumentName) throws CommandSyntaxException {
		try {
			return context.getArgument(argumentName, Origin.class);
		} catch (IllegalArgumentException e) {
			throw ORIGIN_NOT_FOUND.create(null);
		}

	}

	public static @NotNull ResourceLocation parseNamespacedKey(@NotNull StringReader reader) throws CommandSyntaxException {
		return ResourceLocation.read(reader.getString().split(" ")[3].split(" ")[0]).getOrThrow();
	}

	@Override
	public @NotNull Origin parse(@NotNull StringReader reader) throws CommandSyntaxException {
		return OriginsPaper.getOrigin(ResourceLocation.read(reader));
	}

	@Override
	public @NotNull ArgumentType<NamespacedKey> getNativeType() {
		return ArgumentTypes.namespacedKey();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {

		List<ResourceLocation> availableOrigins = new LinkedList<>();
		availableOrigins.add(Origin.EMPTY.getId());

		try {
			OriginLayer originLayer = OriginsPaper.getLayer(parseNamespacedKey(new StringReader(builder.getInput())));
			if (originLayer != null) {
				availableOrigins.addAll(Util.collapseList(originLayer.getOrigins().stream().map(OriginLayer.ConditionedOrigin::origins).toList()));
			}

		} catch (IllegalArgumentException ignored) {
			ignored.printStackTrace();
		} catch (CommandSyntaxException e) {
			throw new RuntimeException(e);
		}

		return SharedSuggestionProvider.suggestResource(availableOrigins.stream(), builder);

	}

}
