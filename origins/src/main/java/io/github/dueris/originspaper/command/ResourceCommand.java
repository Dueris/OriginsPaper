package io.github.dueris.originspaper.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.github.dueris.originspaper.command.argument.PowerArgumentType;
import io.github.dueris.originspaper.command.argument.PowerHolderArgumentType;
import io.github.dueris.originspaper.command.argument.PowerOperationArgumentType;
import io.github.dueris.originspaper.power.type.CooldownPower;
import io.github.dueris.originspaper.power.type.ResourcePower;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public class ResourceCommand {
	private static final DynamicCommandExceptionType ERROR_OBJECTIVE_NOT_FOUND = new DynamicCommandExceptionType(
		name -> Component.translatableEscape("arguments.objective.notFound", name)
	);

	public static void register(@NotNull Commands dispatcher) {
		dispatcher.register(
			literal("resource").requires(cs -> ((net.minecraft.commands.CommandSourceStack) cs).hasPermission(2))
				.then(literal("has")
					.then(argument("target", PowerHolderArgumentType.entity())
						.then(argument("power", PowerArgumentType.power())
							.executes((command) -> resource(command, SubCommand.HAS))))
				)
				.then(literal("get")
					.then(argument("target", PowerHolderArgumentType.entity())
						.then(argument("power", PowerArgumentType.power())
							.executes((command) -> resource(command, SubCommand.GET))))
				)
				.then(literal("set")
					.then(argument("target", PowerHolderArgumentType.entity())
						.then(argument("power", PowerArgumentType.power())
							.then(argument("value", IntegerArgumentType.integer())
								.executes((command) -> resource(command, SubCommand.SET)))))
				)
				.then(literal("change")
					.then(argument("target", PowerHolderArgumentType.entity())
						.then(argument("power", PowerArgumentType.power())
							.then(argument("value", IntegerArgumentType.integer())
								.executes((command) -> resource(command, SubCommand.CHANGE)))))
				)
				.then(literal("operation")
					.then(argument("target", PowerHolderArgumentType.entity())
						.then(argument("power", PowerArgumentType.power())
							.then(argument("operation", PowerOperationArgumentType.operation())
								.then(argument("entity", ScoreHolderArgument.scoreHolder())
									.then(argument("objective", ObjectiveArgument.objective())
										.executes((command) -> resource(command, SubCommand.OPERATION)))))))
				).build()
		);
	}

	private static int resource(@NotNull CommandContext<CommandSourceStack> context, @NotNull SubCommand subCommand) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack source = (net.minecraft.commands.CommandSourceStack) context.getSource();
		LivingEntity target = PowerHolderArgumentType.getHolder(context, "target");

		PowerType power = PowerArgumentType.getPower(context, "power");

		return switch (subCommand) {
			case HAS -> {

				if (isPowerInvalid(power)) {
					source.sendFailure(Component.translatableEscape("commands.execute.conditional.fail"));
					yield 0;
				}

				source.sendSuccess(() -> Component.translatableEscape("commands.execute.conditional.pass"), true);
				yield 1;

			}
			case GET -> {

				if (isPowerInvalid(power)) {
					source.sendFailure(Component.translatableEscape("commands.scoreboard.players.get.null", power.getId(), target.getName().getString()));
					yield 0;
				}

				int value = getValue(power, target);
				source.sendSuccess(() -> Component.translatableEscape("commands.scoreboard.players.get.success", target.getName().getString(), value, power.getId()), true);

				yield value;

			}
			case SET -> {

				if (isPowerInvalid(power)) {
					source.sendFailure(Component.translatableEscape("argument.scoreHolder.empty"));
					yield 0;
				}

				int value = IntegerArgumentType.getInteger(context, "value");
				setValue(power, value, target);

				source.sendSuccess(() -> Component.translatableEscape("commands.scoreboard.players.set.success.single", power.getId(), target.getName().getString(), value), true);

				yield value;

			}
			case CHANGE -> {

				if (isPowerInvalid(power)) {
					source.sendFailure(Component.translatableEscape("argument.scoreHolder.empty"));
					yield 0;
				}

				int value = IntegerArgumentType.getInteger(context, "value");
				int total = getValue(power, target) + value;

				setValue(power, total, target);

				source.sendSuccess(() -> Component.translatableEscape("commands.scoreboard.players.add.success.single", value, power.getId(), target.getName().getString(), total), true);
				yield total;

			}
			case OPERATION -> {

				if (isPowerInvalid(power)) {
					source.sendFailure(Component.translatableEscape("argument.scoreHolder.empty"));
					yield 0;
				}

				ScoreHolder scoreHolder = getNames(context, "entity", Collections::emptyList).iterator().next();
				Objective scoreboardObjective = getObjective(context, "objective");

				ScoreAccess scoreAccess = source.getServer().getScoreboard().getOrCreatePlayerScore(scoreHolder, scoreboardObjective);
				context.getArgument("operation", PowerOperationArgumentType.Operation.class).apply(power, scoreAccess, target);

				int value = getValue(power, target);
				source.sendSuccess(() -> Component.translatableEscape("commands.scoreboard.players.operation.success.single", power.getId(), target.getName().getString(), value), true);

				yield value;

			}
		};

	}

	private static int getValue(PowerType powerType, ScoreHolder entity) {
		if (powerType instanceof ResourcePower vip) {
			return vip.getValue(entity);
		} else if (powerType instanceof CooldownPower cp) {
			return cp.getRemainingTicks(entity);
		} else {
			return 0;
		}
	}

	private static void setValue(PowerType powerType, int newValue, ScoreHolder entity) {
		if (powerType instanceof ResourcePower vip) {
			vip.setValue(entity, newValue);
		} else if (powerType instanceof CooldownPower cp) {
			cp.setCooldown(entity, newValue);
		}
	}

	private static boolean isPowerInvalid(PowerType powerType) {
		return !(powerType instanceof ResourcePower) && !(powerType instanceof CooldownPower);
	}

	public static @NotNull Collection<ScoreHolder> getNames(@NotNull CommandContext<CommandSourceStack> context, String name, Supplier<Collection<ScoreHolder>> players) throws CommandSyntaxException {
		Collection<ScoreHolder> collection = context.getArgument(name, ScoreHolderArgument.Result.class).getNames((net.minecraft.commands.CommandSourceStack) context.getSource(), players);
		if (collection.isEmpty()) {
			throw EntityArgument.NO_ENTITIES_FOUND.create();
		} else {
			return collection;
		}
	}

	public static @NotNull Objective getObjective(@NotNull CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		String string = context.getArgument(name, String.class);
		Scoreboard scoreboard = ((net.minecraft.commands.CommandSourceStack) context.getSource()).getServer().getScoreboard();
		Objective objective = scoreboard.getObjective(string);
		if (objective == null) {
			throw ERROR_OBJECTIVE_NOT_FOUND.create(string);
		} else {
			return objective;
		}
	}

	public enum SubCommand {
		HAS, GET, SET, CHANGE, OPERATION
	}
}
