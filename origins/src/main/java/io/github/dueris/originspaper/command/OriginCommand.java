package io.github.dueris.originspaper.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.command.argument.LayerArgumentType;
import io.github.dueris.originspaper.command.argument.OriginArgumentType;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.storage.OriginComponent;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.LangFile;
import io.github.dueris.originspaper.util.Util;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public class OriginCommand {
	public static void register(@NotNull Commands dispatcher) {
		dispatcher.register(
			literal("origin").requires(cs -> ((net.minecraft.commands.CommandSourceStack) cs).hasPermission(2))
				.then(literal("set")
					.then(argument("targets", EntityArgument.players())
						.then(argument("layer", LayerArgumentType.layer())
							.then(argument("origin", OriginArgumentType.origin())
								.executes(OriginCommand::setOrigin))))
				)
				.then(literal("has")
					.then(argument("targets", EntityArgument.players())
						.then(argument("layer", LayerArgumentType.layer())
							.then(argument("origin", OriginArgumentType.origin())
								.executes(OriginCommand::hasOrigin))))
				)
				.then(literal("get")
					.then(argument("target", EntityArgument.player())
						.then(argument("layer", LayerArgumentType.layer())
							.executes(OriginCommand::getOrigin)
						)
					)
				)
				.then(literal("gui")
					.executes(commandContext -> OriginCommand.openMultipleLayerScreens(commandContext, TargetType.INVOKER))
					.then(argument("targets", EntityArgument.players())
						.executes(commandContext -> OriginCommand.openMultipleLayerScreens(commandContext, TargetType.SPECIFY))
						.then(argument("layer", LayerArgumentType.layer())
							.executes(OriginCommand::openSingleLayerScreen)
						)
					)
				)
				.then(literal("random")
					.executes(commandContext -> OriginCommand.randomizeOrigins(commandContext, TargetType.INVOKER))
					.then(argument("targets", EntityArgument.players())
						.executes(commandContext -> OriginCommand.randomizeOrigins(commandContext, TargetType.SPECIFY))
						.then(argument("layer", LayerArgumentType.layer())
							.executes(OriginCommand::randomizeOrigin)
						)
					)
				).build()
		);
	}

	/**
	 * Set the origin of the specified entities in the specified origin layer.
	 *
	 * @param commandContext the command context
	 * @return the number of players whose origin has been set
	 * @throws CommandSyntaxException if the entity is not found or if the entity is <b>not</b> an instance of {@link ServerPlayer}
	 */
	private static int setOrigin(@NotNull CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

		Collection<ServerPlayer> targets = commandContext.getArgument("targets", EntitySelector.class).findPlayers((net.minecraft.commands.CommandSourceStack) commandContext.getSource());
		OriginLayer originLayer = LayerArgumentType.getLayer(commandContext, "layer");
		Origin origin = OriginArgumentType.getOrigin(commandContext, "origin");
		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();

		int processedTargets = 0;

		if (origin.equals(Origin.EMPTY) || Util.collapseList(originLayer.getOrigins().stream().map(OriginLayer.ConditionedOrigin::origins).toList()).contains(origin.getId())) {

			for (ServerPlayer target : targets) {

				OriginComponent.setOrigin(target.getBukkitEntity(), originLayer, origin);
				processedTargets++;

			}

			if (processedTargets == 1)
				serverCommandSource.sendSuccess(() -> LangFile.translatable("commands.origin.set.success.single", targets.iterator().next().getDisplayName().getString(), originLayer.getName(), origin.getName()), true);
			else {
				int finalProcessedTargets = processedTargets;
				serverCommandSource.sendSuccess(() -> LangFile.translatable("commands.origin.set.success.multiple", finalProcessedTargets, originLayer.getName(), origin.getName()), true);
			}

		} else
			serverCommandSource.sendFailure(LangFile.translatable("commands.origin.unregistered_in_layer", origin.getId(), originLayer.getId()));

		return processedTargets;

	}

	/**
	 * Check if the specified entities has the specified origin in the specified origin layer.
	 *
	 * @param commandContext the command context
	 * @return the number of players that has the specified origin in the specified origin layer
	 * @throws CommandSyntaxException if the entity is not found or if the entity is <b>not</b> an instance of {@link ServerPlayer}
	 */
	private static int hasOrigin(@NotNull CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

		Collection<ServerPlayer> targets = commandContext.getArgument("targets", EntitySelector.class).findPlayers((net.minecraft.commands.CommandSourceStack) commandContext.getSource());
		OriginLayer originLayer = LayerArgumentType.getLayer(commandContext, "layer");
		Origin origin = OriginArgumentType.getOrigin(commandContext, "origin");
		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();

		int processedTargets = 0;

		if (origin.equals(Origin.EMPTY) || originLayer.getOrigins().contains(origin.getId())) {

			for (ServerPlayer target : targets) {
				if ((origin.equals(Origin.EMPTY) || OriginComponent.hasOrigin(target.getBukkitEntity(), originLayer)) && OriginComponent.getOrigin(target.getBukkitEntity(), originLayer).equals(origin))
					processedTargets++;
			}

			if (processedTargets == 0)
				serverCommandSource.sendFailure(LangFile.translatable("commands.execute.conditional.fail"));
			else if (processedTargets == 1)
				serverCommandSource.sendSuccess(() -> LangFile.translatable("commands.execute.conditional.pass"), true);
			else {
				int finalProcessedTargets = processedTargets;
				serverCommandSource.sendSuccess(() -> LangFile.translatable("commands.execute.conditional.pass_count", finalProcessedTargets), true);
			}

		} else
			serverCommandSource.sendFailure(LangFile.translatable("commands.origin.unregistered_in_layer", origin.getId(), originLayer.getId()));

		return processedTargets;

	}

	/**
	 * Get the origin of the specified entity from the specified origin layer.
	 *
	 * @param commandContext the command context
	 * @return 1
	 * @throws CommandSyntaxException if the entity is not found or if the entity is <b>not</b> an instance of {@link ServerPlayer}
	 */
	private static int getOrigin(@NotNull CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

		ServerPlayer target = commandContext.getArgument("target", EntitySelector.class).findSinglePlayer((net.minecraft.commands.CommandSourceStack) commandContext.getSource());
		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();

		OriginLayer originLayer = LayerArgumentType.getLayer(commandContext, "layer");
		Origin origin = OriginComponent.getOrigin(target.getBukkitEntity(), originLayer);

		serverCommandSource.sendSuccess(() -> LangFile.translatable("commands.origin.get.result", target.getDisplayName().getString(), originLayer.getName(), origin.getName(), origin.getId()), true);

		return 1;

	}

	/**
	 * Open the 'Choose Origin' screen for the specified origin layer to the specified entities.
	 *
	 * @param commandContext the command context
	 * @return the number of players that had the 'Choose Origin' screen opened for them
	 * @throws CommandSyntaxException if the entity is not found or if the entity is not an instance of {@link ServerPlayer}
	 */
	private static int openSingleLayerScreen(@NotNull CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();
		Collection<ServerPlayer> targets = commandContext.getArgument("targets", EntitySelector.class).findPlayers((net.minecraft.commands.CommandSourceStack) commandContext.getSource());
		OriginLayer originLayer = LayerArgumentType.getLayer(commandContext, "layer");

		for (ServerPlayer target : targets) {
			PowerHolderComponent.unloadPowers(target.getBukkitEntity(), originLayer, true);
			OriginComponent.setOrigin(target.getBukkitEntity(), originLayer, Origin.EMPTY);
		}

		serverCommandSource.sendSuccess(() -> LangFile.translatable("commands.origin.gui.layer", targets.size(), originLayer.getName()), true);
		return targets.size();

	}

	/**
	 * Open the 'Choose Origin' screen for all the enabled origin layers to the specified entities.
	 *
	 * @param commandContext the command context
	 * @return the number of players that had the 'Choose Origin' screen opened for them
	 * @throws CommandSyntaxException if the entity is not found or if the entity is not an instance of {@link ServerPlayer}
	 */
	private static int openMultipleLayerScreens(@NotNull CommandContext<CommandSourceStack> commandContext, @NotNull TargetType targetType) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();
		List<ServerPlayer> targets = new LinkedList<>();

		switch (targetType) {
			case INVOKER -> targets.add(serverCommandSource.getPlayerOrException());
			case SPECIFY ->
				targets.addAll(commandContext.getArgument("targets", EntitySelector.class).findPlayers((net.minecraft.commands.CommandSourceStack) commandContext.getSource()));
		}

		for (ServerPlayer target : targets) {
			for (OriginLayer layer : OriginComponent.getLayers(target.getBukkitEntity())) {
				PowerHolderComponent.unloadPowers(target.getBukkitEntity(), layer, true);
				OriginComponent.setOrigin(target.getBukkitEntity(), layer, Origin.EMPTY);
			}
		}

		serverCommandSource.sendSuccess(() -> LangFile.translatable("commands.origin.gui.all", targets.size()), false);
		return targets.size();

	}

	/**
	 * Randomize the origin of the specified entities in the specified origin layer.
	 *
	 * @param commandContext the command context
	 * @return the number of players that had their origin randomized in the specified origin layer
	 * @throws CommandSyntaxException if the entity is not found or if the entity is not an instance of {@link ServerPlayer}
	 */
	private static int randomizeOrigin(@NotNull CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();
		Collection<ServerPlayer> targets = commandContext.getArgument("targets", EntitySelector.class).findPlayers((net.minecraft.commands.CommandSourceStack) commandContext.getSource());
		OriginLayer originLayer = LayerArgumentType.getLayer(commandContext, "layer");

		if (originLayer.isRandomAllowed()) {

			Origin origin = null;
			for (ServerPlayer target : targets) {
				origin = getRandomOrigin(target, originLayer);
			}

			if (targets.size() > 1)
				serverCommandSource.sendSuccess(() -> LangFile.translatable("commands.origin.random.success.multiple", targets.size(), originLayer.getName()), true);
			else if (targets.size() == 1) {
				Origin finalOrigin = origin;
				serverCommandSource.sendSuccess(() -> LangFile.translatable("commands.origin.random.success.single", targets.iterator().next().getDisplayName().getString(), finalOrigin.getName(), originLayer.getName()), false);
			}

			return targets.size();

		} else {
			serverCommandSource.sendFailure(LangFile.translatable("commands.origin.random.not_allowed", originLayer.getName()));
			return 0;
		}

	}

	/**
	 * Randomize the origins of the specified entities in all the origin layers that allows to be randomized.
	 *
	 * @param commandContext the command context
	 * @return the number of players that had their origins randomized in all the origin layers that allows to be randomized
	 * @throws CommandSyntaxException if the entity is not found or if the entity is not an instance of {@link ServerPlayer}
	 */
	private static int randomizeOrigins(@NotNull CommandContext<CommandSourceStack> commandContext, @NotNull TargetType targetType) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();
		List<ServerPlayer> targets = new LinkedList<>();
		List<OriginLayer> originLayers = OriginsPaper.getRegistry().retrieve(Registries.LAYER).stream().filter(OriginLayer::isRandomAllowed).toList();

		switch (targetType) {
			case INVOKER -> targets.add(serverCommandSource.getPlayerOrException());
			case SPECIFY ->
				targets.addAll(commandContext.getArgument("targets", EntitySelector.class).findPlayers((net.minecraft.commands.CommandSourceStack) commandContext.getSource()));
		}

		for (ServerPlayer target : targets) {
			for (OriginLayer originLayer : originLayers) {
				getRandomOrigin(target, originLayer);
			}
		}

		serverCommandSource.sendSuccess(() -> LangFile.translatable("commands.origin.random.all", targets.size(), originLayers.size()), false);
		return targets.size();

	}

	private static @NotNull Origin getRandomOrigin(@NotNull ServerPlayer target, @NotNull OriginLayer originLayer) {

		List<Origin> origins = originLayer.getRandomOrigins();
		Origin origin = origins.get(new Random().nextInt(origins.size()));

		OriginComponent.setOrigin(target.getBukkitEntity(), originLayer, origin);

		OriginsPaper.LOGGER.info(
			"Player {} was randomly assigned the origin {} for layer {}",
			target.getDisplayName().getString(),
			origin.getId().toString(),
			originLayer.getId().toString()
		);

		return origin;

	}

	private enum TargetType {
		INVOKER,
		SPECIFY
	}
}
