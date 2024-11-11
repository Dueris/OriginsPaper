package io.github.dueris.originspaper.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DataResult;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerManager;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.util.PowerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public record PowerArgumentType(PowerTarget powerTarget) implements CustomArgumentType<Power, NamespacedKey> {

	public static final DynamicCommandExceptionType POWER_NOT_RESOURCE = new DynamicCommandExceptionType(
		o -> Component.translatableEscape("commands.apoli.power_not_resource", o)
	);

	public static final Dynamic2CommandExceptionType POWER_NOT_GRANTED = new Dynamic2CommandExceptionType(
		(a, b) -> Component.translatable("commands.apoli.power_not_granted", a, b)
	);

	public static final DynamicCommandExceptionType POWER_NOT_FOUND = new DynamicCommandExceptionType(
		o -> Component.translatableEscape("commands.apoli.power_not_found", o)
	);

	public static @NotNull PowerArgumentType power() {
		return new PowerArgumentType(PowerTarget.GENERAL);
	}

	public static Power getPower(@NotNull CommandContext<CommandSourceStack> context, String argumentName) {
		return context.getArgument(argumentName, Power.class);
	}

	public static @NotNull PowerArgumentType resource() {
		return new PowerArgumentType(PowerTarget.RESOURCE);
	}

	public static Power getResource(CommandContext<CommandSourceStack> context, String argumentName) throws CommandSyntaxException {
		Power power = getPower(context, argumentName);
		return PowerUtil.validateResource(power.getPowerType())
			.map(PowerType::getPower)
			.getOrThrow(err -> POWER_NOT_RESOURCE.create(power.getId()));
	}

	@Override
	public @NotNull Power parse(@NotNull StringReader reader) throws CommandSyntaxException {

		ResourceLocation id = ResourceLocation.readNonEmpty(reader);
		DataResult<Power> powerResult = PowerManager.getResult(id);

		if (powerResult.isError()) {
			throw POWER_NOT_FOUND.createWithContext(reader, id);
		} else {

			powerResult = powerResult.flatMap(power -> powerTarget() == PowerTarget.RESOURCE
				? PowerUtil.validateResource(power.getPowerType()).map(PowerType::getPower)
				: DataResult.success(power));

			return powerResult.getOrThrow(err -> POWER_NOT_RESOURCE.createWithContext(reader, id));

		}

	}

	@Override
	public @NotNull ArgumentType<NamespacedKey> getNativeType() {
		return ArgumentTypes.namespacedKey();
	}

	@Override
	public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {

		Stream<ResourceLocation> powerIds = PowerManager.entrySet()
			.stream()
			.filter(e -> powerTarget() != PowerTarget.RESOURCE || PowerUtil.validateResource(e.getValue().getPowerType()).isSuccess())
			.map(Map.Entry::getKey);

		return SharedSuggestionProvider.suggestResource(powerIds, builder);

	}

	public enum PowerTarget {
		GENERAL,
		RESOURCE
	}

}
