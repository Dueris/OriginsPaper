package io.github.dueris.originspaper.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.type.CooldownPower;
import io.github.dueris.originspaper.power.type.ResourcePower;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.ScoreAccess;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class PowerOperationArgumentType implements CustomArgumentType<PowerOperationArgumentType.Operation, String> {

	public static final SimpleCommandExceptionType INVALID_OPERATION = new SimpleCommandExceptionType(Component.translatable("arguments.operation.invalid"));
	public static final SimpleCommandExceptionType DIVISION_ZERO_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("arguments.operation.div0"));

	public static @NotNull PowerOperationArgumentType operation() {
		return new PowerOperationArgumentType();
	}

	@Override
	public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, builder);
	}

	@Override
	public PowerOperationArgumentType.@NotNull Operation parse(@NotNull StringReader stringReader) throws CommandSyntaxException {
		if (!stringReader.canRead()) throw INVALID_OPERATION.create();

		int i = stringReader.getCursor();
		while (stringReader.canRead() && stringReader.peek() != ' ') stringReader.skip();

		String stringOperator = stringReader.getString().substring(i, stringReader.getCursor());
		return switch (stringOperator) {
			case "=" -> (power, score, entity) -> {
				if (power instanceof ResourcePower) {
					((ResourcePower) power).setValue(entity, score.get());
				} else if (power instanceof CooldownPower) {
					((CooldownPower) power).setCooldown(entity, score.get());
				}
			};
			case "+=" -> (power, score, entity) -> {
				if (power instanceof ResourcePower) {
					((ResourcePower) power).setValue(entity, ((ResourcePower) power).getValue(entity) + score.get());
				} else if (power instanceof CooldownPower cooldownPower) {
					cooldownPower.setCooldown(entity, cooldownPower.getRemainingTicks(entity) + score.get());
				}
			};
			case "-=" -> (power, score, entity) -> {
				if (power instanceof ResourcePower) {
					((ResourcePower) power).setValue(entity, ((ResourcePower) power).getValue(entity) - score.get());
				} else if (power instanceof CooldownPower cooldownPower) {
					cooldownPower.setCooldown(entity, cooldownPower.getRemainingTicks(entity) - score.get());
				}
			};
			case "*=" -> (power, score, entity) -> {
				if (power instanceof ResourcePower) {
					((ResourcePower) power).setValue(entity, ((ResourcePower) power).getValue(entity) * score.get());
				} else if (power instanceof CooldownPower) {
					((CooldownPower) power).setCooldown(entity, ((CooldownPower) power).getRemainingTicks(entity) * score.get());
				}
			};
			case "/=" -> (power, score, entity) -> {
				if (power instanceof ResourcePower resource) {
					int r = resource.getValue(entity);
					int s = score.get();
					if (s == 0) {
						throw DIVISION_ZERO_EXCEPTION.create();
					} else {
						resource.setValue(entity, Math.floorDiv(r, s));
					}
				} else if (power instanceof CooldownPower cooldownPower) {
					int c = cooldownPower.getRemainingTicks(entity);
					int s = score.get();
					if (s == 0) {
						throw DIVISION_ZERO_EXCEPTION.create();
					} else {
						cooldownPower.setCooldown(entity, Math.floorDiv(c, s));
					}
				}
			};
			case "%=" -> (power, score, entity) -> {
				if (power instanceof ResourcePower resource) {
					int r = resource.getValue(entity);
					int s = score.get();
					if (s == 0) {
						throw DIVISION_ZERO_EXCEPTION.create();
					} else {
						resource.setValue(entity, Math.floorMod(r, s));
					}
				} else if (power instanceof CooldownPower cooldownPower) {
					int c = cooldownPower.getRemainingTicks(entity);
					int s = score.get();
					if (s == 0) {
						throw DIVISION_ZERO_EXCEPTION.create();
					} else {
						cooldownPower.setCooldown(entity, Math.floorMod(c, s));
					}
				}
			};
			case "<" -> (power, score, entity) -> {
				if (power instanceof ResourcePower resource) {
					resource.setValue(entity, Math.min(resource.getValue(entity), score.get()));
				} else if (power instanceof CooldownPower cooldownPower) {
					cooldownPower.setCooldown(entity, Math.min(cooldownPower.getRemainingTicks(entity), score.get()));
				}
			};
			case ">" -> (power, score, entity) -> {
				if (power instanceof ResourcePower resource) {
					resource.setValue(entity, Math.max(resource.getValue(entity), score.get()));
				} else if (power instanceof CooldownPower cooldownPower) {
					cooldownPower.setCooldown(entity, Math.max(cooldownPower.getRemainingTicks(entity), score.get()));
				}
			};
			case "><" -> (power, score, entity) -> {
				if (power instanceof ResourcePower resource) {
					int v = score.get();
					score.set(resource.getValue(entity));
					resource.setValue(entity, v);
				} else if (power instanceof CooldownPower cooldownPower) {
					int v = score.get();
					score.set(cooldownPower.getRemainingTicks(entity));
					cooldownPower.setCooldown(entity, v);
				}
			};
			default -> throw INVALID_OPERATION.create();
		};
	}

	@Override
	public @NotNull ArgumentType<String> getNativeType() {
		return StringArgumentType.string();
	}

	public interface Operation {
		void apply(PowerType powerType, ScoreAccess score, Entity entity) throws CommandSyntaxException;
	}
}
