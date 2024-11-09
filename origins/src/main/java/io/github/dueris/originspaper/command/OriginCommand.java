package io.github.dueris.originspaper.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.command.argument.LayerArgumentType;
import io.github.dueris.originspaper.command.argument.OriginArgumentType;
import io.github.dueris.originspaper.component.OriginComponent;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.origin.OriginLayerManager;
import io.github.dueris.originspaper.origin.OriginManager;
import io.github.dueris.originspaper.screen.ChooseOriginScreen;
import io.github.dueris.originspaper.util.Util;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

@SuppressWarnings("UnstableApiUsage")
public class OriginCommand {

	public static LiteralCommandNode<CommandSourceStack> node() {
		return literal("origin").requires(cs -> ((net.minecraft.commands.CommandSourceStack) cs).hasPermission(2))
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
			).build();
	}

	/**
	 * Set the origin of the specified entities in the specified origin layer.
	 *
	 * @param commandContext the command context
	 * @return the number of players whose origin has been set
	 * @throws CommandSyntaxException if the entity is not found or if the entity is <b>not</b> an instance of {@link ServerPlayer}
	 */
	private static int setOrigin(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

		Collection<ServerPlayer> targets = Util.getPlayers(commandContext, "targets");
		OriginLayer originLayer = LayerArgumentType.getLayer(commandContext, "layer");
		Origin origin = OriginArgumentType.getOrigin(commandContext, "origin");
		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();

		int processedTargets = 0;

		if (origin.equals(Origin.EMPTY) || originLayer.getOrigins().contains(origin.getId())) {

			for (ServerPlayer target : targets) {

				OriginComponent originComponent = OriginComponent.ORIGIN.get(target);
				boolean hadOriginBefore = originComponent.hadOriginBefore();

				originComponent.setOrigin(originLayer, origin);

				OriginComponent.partialOnChosen(target, hadOriginBefore, origin);
				processedTargets++;

			}

			if (processedTargets == 1)
				serverCommandSource.sendSuccess(() -> Component.translatable("commands.origin.set.success.single", targets.iterator().next().getName().getString(), originLayer.getName(), origin.getName()), true);
			else {
				int finalProcessedTargets = processedTargets;
				serverCommandSource.sendSuccess(() -> Component.translatable("commands.origin.set.success.multiple", finalProcessedTargets, originLayer.getName(), origin.getName()), true);
			}

		} else
			serverCommandSource.sendFailure(Component.translatable("commands.origin.unregistered_in_layer", origin.getId(), originLayer.getId()));

		return processedTargets;

	}

	/**
	 * Check if the specified entities has the specified origin in the specified origin layer.
	 *
	 * @param commandContext the command context
	 * @return the number of players that has the specified origin in the specified origin layer
	 * @throws CommandSyntaxException if the entity is not found or if the entity is <b>not</b> an instance of {@link ServerPlayer}
	 */
	private static int hasOrigin(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

		Collection<ServerPlayer> targets = Util.getPlayers(commandContext, "targets");
		OriginLayer originLayer = LayerArgumentType.getLayer(commandContext, "layer");
		Origin origin = OriginArgumentType.getOrigin(commandContext, "origin");
		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();

		int processedTargets = 0;

		if (origin.equals(Origin.EMPTY) || originLayer.getOrigins().contains(origin.getId())) {

			for (ServerPlayer target : targets) {
				OriginComponent originComponent = OriginComponent.ORIGIN.get(target);
				if ((origin.equals(Origin.EMPTY) || originComponent.hasOrigin(originLayer)) && originComponent.getOrigin(originLayer).equals(origin))
					processedTargets++;
			}

			if (processedTargets == 0)
				serverCommandSource.sendFailure(Component.translatable("commands.execute.conditional.fail"));
			else if (processedTargets == 1)
				serverCommandSource.sendSuccess(() -> Component.translatable("commands.execute.conditional.pass"), true);
			else {
				int finalProcessedTargets = processedTargets;
				serverCommandSource.sendSuccess(() -> Component.translatable("commands.execute.conditional.pass_count", finalProcessedTargets), true);
			}

		} else
			serverCommandSource.sendFailure(Component.translatable("commands.origin.unregistered_in_layer", origin.getId(), originLayer.getId()));

		return processedTargets;

	}

	/**
	 * Get the origin of the specified entity from the specified origin layer.
	 *
	 * @param commandContext the command context
	 * @return 1
	 * @throws CommandSyntaxException if the entity is not found or if the entity is <b>not</b> an instance of {@link ServerPlayer}
	 */
	private static int getOrigin(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

		ServerPlayer target = Util.getPlayer(commandContext, "target");
		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();

		OriginComponent originComponent = OriginComponent.ORIGIN.get(target);
		OriginLayer originLayer = LayerArgumentType.getLayer(commandContext, "layer");
		Origin origin = originComponent.getOrigin(originLayer);

		serverCommandSource.getBukkitSender().sendMessage(net.kyori.adventure.text.Component.translatable("commands.origin.get.result").arguments(
			PaperAdventure.asAdventure(target.getName()), PaperAdventure.asAdventure(originLayer.getName()), PaperAdventure.asAdventure(origin.getName()), net.kyori.adventure.text.Component.text(origin.getId().toString())
		));

		return 1;

	}

	/**
	 * Open the 'Choose Origin' screen for the specified origin layer to the specified entities.
	 *
	 * @param commandContext the command context
	 * @return the number of players that had the 'Choose Origin' screen opened for them
	 * @throws CommandSyntaxException if the entity is not found or if the entity is not an instance of {@link ServerPlayer}
	 */
	private static int openSingleLayerScreen(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();
		Collection<ServerPlayer> targets = Util.getPlayers(commandContext, "targets");
		OriginLayer originLayer = LayerArgumentType.getLayer(commandContext, "layer");

		for (ServerPlayer target : targets) {
			openLayerScreen(target, originLayer);
		}

		serverCommandSource.sendSuccess(() -> Component.translatable("commands.origin.gui.layer", targets.size(), originLayer.getName()), true);
		return targets.size();

	}

	/**
	 * Open the 'Choose Origin' screen for all the enabled origin layers to the specified entities.
	 *
	 * @param commandContext the command context
	 * @return the number of players that had the 'Choose Origin' screen opened for them
	 * @throws CommandSyntaxException if the entity is not found or if the entity is not an instance of {@link ServerPlayer}
	 */
	private static int openMultipleLayerScreens(CommandContext<CommandSourceStack> commandContext, TargetType targetType) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();
		List<ServerPlayer> targets = new ArrayList<>();

		switch (targetType) {
			case INVOKER -> targets.add(serverCommandSource.getPlayerOrException());
			case SPECIFY -> targets.addAll(Util.getPlayers(commandContext, "targets"));
		}

		for (ServerPlayer target : targets) {
			openLayerScreen(target);
		}

		serverCommandSource.sendSuccess(() -> Component.translatable("commands.origin.gui.all", targets.size()), false);
		return targets.size();

	}

	/**
	 * Randomize the origin of the specified entities in the specified origin layer.
	 *
	 * @param commandContext the command context
	 * @return the number of players that had their origin randomized in the specified origin layer
	 * @throws CommandSyntaxException if the entity is not found or if the entity is not an instance of {@link ServerPlayer}
	 */
	private static int randomizeOrigin(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();
		Collection<ServerPlayer> targets = Util.getPlayers(commandContext, "targets");
		OriginLayer originLayer = LayerArgumentType.getLayer(commandContext, "layer");

		if (originLayer.isRandomAllowed()) {

			Origin origin = null;
			for (ServerPlayer target : targets) {
				origin = getRandomOrigin(target, originLayer);
			}

			if (targets.size() > 1)
				serverCommandSource.sendSuccess(() -> Component.translatable("commands.origin.random.success.multiple", targets.size(), originLayer.getName()), true);
			else if (targets.size() == 1) {
				Origin finalOrigin = origin;
				serverCommandSource.sendSuccess(() -> Component.translatable("commands.origin.random.success.single", targets.iterator().next().getName().getString(), finalOrigin.getName(), originLayer.getName()), false);
			}

			return targets.size();

		} else {
			serverCommandSource.sendFailure(Component.translatable("commands.origin.random.not_allowed", originLayer.getName()));
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
	private static int randomizeOrigins(CommandContext<CommandSourceStack> commandContext, TargetType targetType) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack serverCommandSource = (net.minecraft.commands.CommandSourceStack) commandContext.getSource();
		List<ServerPlayer> targets = new ArrayList<>();
		List<OriginLayer> originLayers = OriginLayerManager.values()
			.stream()
			.filter(OriginLayer::isRandomAllowed)
			.toList();

		switch (targetType) {
			case INVOKER -> targets.add(serverCommandSource.getPlayerOrException());
			case SPECIFY -> targets.addAll(Util.getPlayers(commandContext, "targets"));
		}

		for (ServerPlayer target : targets) {
			for (OriginLayer originLayer : originLayers) {
				getRandomOrigin(target, originLayer);
			}
		}

		serverCommandSource.sendSuccess(() -> Component.translatable("commands.origin.random.all", targets.size(), originLayers.size()), false);
		return targets.size();

	}

	private static void openLayerScreen(ServerPlayer target) {
		openLayerScreen(target, null);
	}

	private static void openLayerScreen(ServerPlayer target, @Nullable OriginLayer layer) {

		OriginComponent component = OriginComponent.ORIGIN.get(target);
		List<OriginLayer> layersToProcess = new LinkedList<>();

		if (layer != null) {
			layersToProcess.add(layer);
		} else {
			layersToProcess.addAll(OriginLayerManager.values());
		}

		layersToProcess
			.stream()
			.filter(OriginLayer::isEnabled)
			.forEach(ol -> component.setOrigin(ol, Origin.EMPTY));

		boolean originAutomaticallyAssigned = component.checkAutoChoosingLayers(target, false);
		int originOptions = layer != null ? layer.getOriginOptionCount(target) : OriginLayerManager.getOriginOptionCount(target);

		component.selectingOrigin(!originAutomaticallyAssigned || originOptions > 0);
		component.sync();

		if (component.isSelectingOrigin()) {
			new ChooseOriginScreen(target);
		}

	}

	private static Origin getRandomOrigin(ServerPlayer target, OriginLayer originLayer) {

		List<Origin> origins = originLayer.getRandomOrigins(target).stream().map(OriginManager::get).toList();
		OriginComponent originComponent = OriginComponent.ORIGIN.get(target);
		Origin origin = origins.get(new Random().nextInt(origins.size()));

		boolean hadOriginBefore = originComponent.hadOriginBefore();
		boolean hadAllOrigins = originComponent.hasAllOrigins();

		originComponent.setOrigin(originLayer, origin);
		originComponent.checkAutoChoosingLayers(target, false);
		originComponent.sync();

		if (originComponent.hasAllOrigins() && !hadAllOrigins) OriginComponent.onChosen(target, hadOriginBefore);

		OriginsPaper.LOGGER.info(
			"Player {} was randomly assigned the origin {} for layer {}",
			target.getName().getString(),
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
