package me.dueris.originspaper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import joptsimple.internal.Strings;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.OriginLayer;
import me.dueris.originspaper.util.JsonTextFormatter;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import me.dueris.originspaper.util.entity.PowerUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class PowerCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> main = net.minecraft.commands.Commands.literal("power")
			.requires(source -> source.hasPermission(2));
		(main.then(
			net.minecraft.commands.Commands.literal("dump")
				.then(
					net.minecraft.commands.Commands.argument("power", ResourceLocationArgument.id())
						.suggests(
							(context, builder) -> {
								OriginCommand.POWERS
									.forEach(
										(location, power) -> {
											if (context.getInput().split(" ").length == 2
												|| power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
												|| power.getTag().split(":")[1]
												.startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])) {
												builder.suggest(power.getTag());
											}
										}
									);
								return builder.buildFuture();
							}
						)
						.executes(
							context -> {
								try {
									PowerType power = OriginsPaper.getPlugin()
										.registry
										.retrieve(Registries.CRAFT_POWER)
										.get(
											ResourceLocation.parse(
												ResourceLocationArgument.getId(context, "power").getNamespace()
													+ ":"
													+ ResourceLocationArgument.getId(context, "power").getPath()
											)
										);
									if (power == null) {
										context.getSource().sendFailure(Component.literal("Power not found."));
										return 1;
									}

									String indent = Strings.repeat(' ', 4);
									context.getSource().sendSuccess(() -> {
										String append = context.getSource().isPlayer() ? "" : "\n";
										return Component.literal(append).append(new JsonTextFormatter(indent).apply(power.sourceObject));
									}, false);
								} catch (Exception var3) {
									var3.printStackTrace();
								}

								return 1;
							}
						)
				)
		))
			.then(
				net.minecraft.commands.Commands.literal("grant")
					.then(
						net.minecraft.commands.Commands.argument("targets", EntityArgument.entities())
							.then(
								(net.minecraft.commands.Commands.argument("power", ResourceLocationArgument.id())
									.suggests(
										(context, builder) -> {
											OriginCommand.POWERS
												.forEach(
													(location, power) -> {
														if (context.getInput().split(" ").length == 3
															|| power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
															|| power.getTag().split(":")[1]
															.startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])) {
															builder.suggest(power.getTag());
														}
													}
												);
											return builder.buildFuture();
										}
									)
									.executes(
										context -> {
											EntityArgument.getPlayers(context, "targets")
												.forEach(
													player -> {
														if (PowerHolderComponent.playerPowerMapping.get(player.getBukkitEntity()) != null) {
															PowerType poweR = OriginsPaper.getPlugin()
																.registry
																.retrieve(Registries.CRAFT_POWER)
																.get(ResourceLocationArgument.getId(context, "power"));
															ArrayList<PowerType> powersToEdit = new ArrayList<>(CraftApoli.getNestedPowerTypes(poweR));
															powersToEdit.add(poweR);

															for (PowerType power : powersToEdit) {
																try {
																	PowerUtils.grantPower(
																		context.getSource().getBukkitSender(),
																		power,
																		player.getBukkitEntity(),
																		CraftApoli.getLayerFromTag("apoli:command"),
																		context.getSource().isSilent()
																	);
																} catch (IllegalAccessException |
																		 InstantiationException var7) {
																	throw new RuntimeException(var7);
																}
															}
														}
													}
												);
											return 1;
										}
									))
									.then(
										net.minecraft.commands.Commands.argument("layer", ResourceLocationArgument.id())
											.executes(
												context -> {
													EntityArgument.getPlayers(context, "targets")
														.forEach(
															player -> {
																if (PowerHolderComponent.playerPowerMapping.get(player.getBukkitEntity()) != null) {
																	PowerType poweR = OriginsPaper.getPlugin()
																		.registry
																		.retrieve(Registries.CRAFT_POWER)
																		.get(ResourceLocationArgument.getId(context, "power"));
																	ArrayList<PowerType> powersToEdit = new ArrayList<>(CraftApoli.getNestedPowerTypes(poweR));
																	powersToEdit.add(poweR);

																	for (PowerType power : powersToEdit) {
																		try {
																			PowerUtils.grantPower(
																				context.getSource().getBukkitSender(),
																				power,
																				player.getBukkitEntity(),
																				CraftApoli.getLayerFromTag(
																					CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer"))
																						.asString()
																				),
																				context.getSource().isSilent()
																			);
																		} catch (IllegalAccessException |
																				 InstantiationException var7) {
																			throw new RuntimeException(var7);
																		}
																	}
																}
															}
														);
													return 1;
												}
											)
									)
							)
					)
			)
			.then(
				net.minecraft.commands.Commands.literal("has")
					.then(
						net.minecraft.commands.Commands.argument("targets", EntityArgument.entities())
							.then(
								net.minecraft.commands.Commands.argument("power", ResourceLocationArgument.id())
									.suggests(
										(context, builder) -> {
											OriginCommand.POWERS
												.forEach(
													(location, power) -> {
														if (context.getInput().split(" ").length == 3
															|| power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
															|| power.getTag().split(":")[1]
															.startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])) {
															builder.suggest(power.getTag());
														}
													}
												);
											return builder.buildFuture();
										}
									)
									.executes(
										context -> {
											AtomicBoolean passed = new AtomicBoolean(false);
											EntityArgument.getPlayers(context, "targets")
												.forEach(
													player -> {
														for (OriginLayer layer : OriginCommand.LAYERS.values()) {
															for (PowerType power : PowerHolderComponent.playerPowerMapping.get(player.getBukkitEntity()).get(layer)) {
																if (!passed.get()
																	&& power.getTag()
																	.equals(
																		CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")).asString()
																	)) {
																	passed.set(true);
																}
															}
														}

														if (passed.get()) {
															context.getSource().sendSystemMessage(Component.literal("Test passed"));
														} else {
															context.getSource().sendFailure(Component.literal("Test failed"));
														}
													}
												);
											return 1;
										}
									)
							)
					)
			)
			.then(
				net.minecraft.commands.Commands.literal("list")
					.then(
						(net.minecraft.commands.Commands.argument("targets", EntityArgument.entities())
							.executes(context -> list(context, false)))
							.then(
								net.minecraft.commands.Commands.argument("sub_powers", BoolArgumentType.bool())
									.executes(context -> list(context, BoolArgumentType.getBool(context, "sub_powers")))
							)
					)
			)
			.then(
				net.minecraft.commands.Commands.literal("clear")
					.then(
						net.minecraft.commands.Commands.argument("targets", EntityArgument.entities())
							.executes(
								context -> {
									EntityArgument.getPlayers(context, "targets")
										.forEach(
											p -> {
												for (PowerType power : PowerHolderComponent.getPowers(p.getBukkitEntity())) {
													CraftApoli.getLayersFromRegistry()
														.forEach(
															layer -> {
																try {
																	PowerUtils.removePower(
																		context.getSource().getBukkitSender(),
																		power,
																		p.getBukkitEntity(),
																		layer,
																		context.getSource().isSilent()
																	);
																} catch (IllegalAccessException |
																		 InstantiationException var5) {
																	throw new RuntimeException(var5);
																}
															}
														);
												}
											}
										);
									return 1;
								}
							)
					)
			);
		addRemoveArg(main, "remove");
		addRemoveArg(main, "revoke");
		dispatcher.register(main);
	}

	private static int list(CommandContext<CommandSourceStack> context, boolean subPowers) throws CommandSyntaxException {
		for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
			ConcurrentLinkedQueue<PowerType> allPowers = new ConcurrentLinkedQueue<>();
			ArrayList<PowerType> powers = PowerHolderComponent.getPowers(player.getBukkitEntity());
//			ArrayList<Multiple> multiples = PowerHolderComponent.getPowers(player.getBukkitEntity(), Multiple.class);
//			if (!subPowers) {
//				powers.removeAll(Util.collapseList(multiples.stream().map(Multiple::getSubPowers).toList()));
//			}

			if (!powers.isEmpty()) {
				allPowers.addAll(powers);
			}

			if (allPowers.isEmpty()) {
				context.getSource()
					.sendFailure(Component.literal("Entity %name% does not have any powers".replace("%name%", player.getBukkitEntity().getName())));
			} else {
				String msg = "Entity %name% has %size% powers: [%powers%]"
					.replace("%name%", player.getBukkitEntity().getName())
					.replace("%size%", String.valueOf(allPowers.size()));
				String[] powerString = new String[]{""};
				allPowers.forEach(power -> powerString[0] = powerString[0] + power.getTag() + ", ");
				String finMsg = msg.replace("%powers%", powerString[0]);
				context.getSource().sendSystemMessage(Component.literal(finMsg.replace(", ]", "]")));
			}
		}

		return 1;
	}

	private static void addRemoveArg(LiteralArgumentBuilder<CommandSourceStack> main, String name) {
		main.then(
			net.minecraft.commands.Commands.literal(name)
				.then(
					net.minecraft.commands.Commands.argument("targets", EntityArgument.entities())
						.then(
							(net.minecraft.commands.Commands.argument("power", ResourceLocationArgument.id())
								.suggests(
									(context, builder) -> {
										OriginCommand.POWERS
											.forEach(
												(location, power) -> {
													if (context.getInput().split(" ").length == 3
														|| power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
														|| power.getTag().split(":")[1]
														.startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])) {
														builder.suggest(power.getTag());
													}
												}
											);
										return builder.buildFuture();
									}
								)
								.executes(
									context -> {
										EntityArgument.getPlayers(context, "targets")
											.forEach(
												p -> {
													ResourceLocation arg = ResourceLocationArgument.getId(context, "power");
													CraftApoli.getLayersFromRegistry()
														.forEach(
															layer -> {
																try {
																	PowerUtils.removePower(
																		context.getSource().getBukkitSender(),
																		OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(arg),
																		p.getBukkitEntity(),
																		layer,
																		context.getSource().isSilent()
																	);
																} catch (IllegalAccessException |
																		 InstantiationException var5) {
																	throw new RuntimeException(var5);
																}
															}
														);
												}
											);
										return 1;
									}
								))
								.then(
									net.minecraft.commands.Commands.argument("layer", ResourceLocationArgument.id())
										.executes(
											context -> {
												EntityArgument.getPlayers(context, "targets")
													.forEach(
														p -> {
															ResourceLocation arg = ResourceLocationArgument.getId(context, "power");
															String layer = CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString();

															try {
																PowerUtils.removePower(
																	context.getSource().getBukkitSender(),
																	OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(arg),
																	p.getBukkitEntity(),
																	CraftApoli.getLayerFromTag(layer),
																	context.getSource().isSilent()
																);
															} catch (IllegalAccessException |
																	 InstantiationException var5) {
																throw new RuntimeException(var5);
															}
														}
													);
												return 1;
											}
										)
								)
						)
				)
		);
	}
}
