package io.github.dueris.originspaper.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.util.PowerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.ScoreAccess;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
// Very similar to OperationArgumentType, but modified to make it work with resources.
public class PowerOperationArgumentType implements CustomArgumentType<PowerOperationArgumentType.Operation, String> {

	public static final SimpleCommandExceptionType INVALID_OPERATION = new SimpleCommandExceptionType(Component.translatable("arguments.operation.invalid"));
	public static final SimpleCommandExceptionType DIVISION_ZERO_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("arguments.operation.div0"));

	public static @NotNull PowerOperationArgumentType operation() {
		return new PowerOperationArgumentType();
	}

	public static Operation getOperation(@NotNull CommandContext<CommandSourceStack> context, String argumentName) {
		return context.getArgument(argumentName, Operation.class);
	}

	@Override
	public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, builder);
	}

	@Override
	public @NotNull Operation parse(@NotNull StringReader stringReader) throws CommandSyntaxException {

		if (!stringReader.canRead()) {
			throw INVALID_OPERATION.create();
		}

		int i = stringReader.getCursor();
		while (stringReader.canRead() && stringReader.peek() != ' ') {
			stringReader.skip();
		}

		String stringOperator = stringReader.getString().substring(i, stringReader.getCursor());
		return switch (stringOperator) {
			case "=" -> (powerType, scoreAccess) ->
				PowerUtil.setResourceValue(powerType, scoreAccess.get());
			case "+=" -> (powerType, scoreAccess) ->
				PowerUtil.changeResourceValue(powerType, scoreAccess.get());
			case "-=" -> (powerType, scoreAccess) ->
				PowerUtil.changeResourceValue(powerType, -scoreAccess.get());
			case "*=" -> (powerType, scoreAccess) ->
				PowerUtil.setResourceValue(powerType, PowerUtil.getResourceValueInt(powerType) * scoreAccess.get());
			case "/=" -> (powerType, scoreAccess) -> {

				int resourceValue = PowerUtil.getResourceValueInt(powerType);
				int scoreValue = scoreAccess.get();

				if (scoreValue == 0) {
					throw DIVISION_ZERO_EXCEPTION.create();
				} else {
					return PowerUtil.setResourceValue(powerType, Math.floorDiv(resourceValue, scoreValue));
				}

			};
			case "%=" -> (powerType, scoreAccess) -> {

				int resourceValue = PowerUtil.getResourceValueInt(powerType);
				int scoreValue = scoreAccess.get();

				if (scoreValue == 0) {
					throw DIVISION_ZERO_EXCEPTION.create();
				} else {
					return PowerUtil.setResourceValue(powerType, Math.floorMod(resourceValue, scoreValue));
				}

			};
			case "<" -> (powerType, scoreAccess) ->
				PowerUtil.setResourceValue(powerType, Math.min(PowerUtil.getResourceValueInt(powerType), scoreAccess.get()));
			case ">" -> (powerType, scoreAccess) ->
				PowerUtil.setResourceValue(powerType, Math.max(PowerUtil.getResourceValueInt(powerType), scoreAccess.get()));
			case "><" -> (powerType, scoreAccess) -> {

				int resourceValue = PowerUtil.getResourceValueInt(powerType);
				int scoreValue = scoreAccess.get();

				scoreAccess.set(resourceValue);
				return PowerUtil.setResourceValue(powerType, scoreValue);

			};
			default -> throw INVALID_OPERATION.create();
		};
	}

	@Override
	public @NotNull ArgumentType<String> getNativeType() {
		return StringArgumentType.string();
	}

	public interface Operation {
		boolean apply(PowerType powerType, ScoreAccess scoreAccess) throws CommandSyntaxException;
	}

}
