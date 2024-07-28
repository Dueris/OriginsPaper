package me.dueris.originspaper.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public class ResourceCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		/*LiteralArgumentBuilder<CommandSourceStack> main = net.minecraft.commands.Commands.literal("resource")
			.requires(source -> source.hasPermission(2));
		(main.then(
			net.minecraft.commands.Commands.literal("has")
				.then(
					net.minecraft.commands.Commands.argument("targets", EntityArgument.player())
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
								.executes(context -> {
									ResourceLocation power = ResourceLocationArgument.getId(context, "power");
									ServerPlayer player = EntityArgument.getPlayer(context, "targets");
									PowerType powerType = CraftApoli.getPower(power.toString());
									if (powerType == null) {
										throw new IllegalArgumentException("Provided power argument was not found!");
									} else {
										if (PowerHolderComponent.hasPower(player.getBukkitEntity(), powerType.getTag())
											&& PowerHolderComponent.isOfType(powerType, Resource.class)) {
											context.getSource().sendSystemMessage(Component.literal("Test passed"));
										} else {
											context.getSource().sendFailure(Component.literal("Test failed"));
										}

										return 1;
									}
								})
						)
				)
		))
			.then(
				net.minecraft.commands.Commands.literal("get")
					.then(
						net.minecraft.commands.Commands.argument("targets", EntityArgument.player())
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
											ResourceLocation power = ResourceLocationArgument.getId(context, "power");
											ServerPlayer player = EntityArgument.getPlayer(context, "targets");
											PowerType powerType = CraftApoli.getPower(power.toString());
											if (powerType == null) {
												throw new IllegalArgumentException("Provided power argument was not found!");
											} else {
												Component failure = Component.literal(
													"Can't get value of {%} for {$}; none is set".replace("{$}", player.displayName).replace("{%}", power.toString())
												);
												if (PowerHolderComponent.hasPower(player.getBukkitEntity(), powerType.getTag())
													&& PowerHolderComponent.isOfType(powerType, Resource.class)) {
													ResourcePower resourcePower = (ResourcePower) powerType;
													Resource.getDisplayedBar(player.getBukkitEntity(), resourcePower.getTag())
														.ifPresentOrElse(
															bar -> {
																Integer mappedProgress = bar.getMappedProgress();
																context.getSource()
																	.sendSystemMessage(
																		Component.literal(
																			"Dueris has {%} {$}".replace("{%}", mappedProgress.toString()).replace("{$}", power.toString())
																		)
																	);
															},
															() -> context.getSource().sendFailure(failure)
														);
												} else {
													context.getSource().sendFailure(failure);
												}

												return 1;
											}
										}
									)
							)
					)
			)
			.then(
				net.minecraft.commands.Commands.literal("set")
					.then(
						net.minecraft.commands.Commands.argument("targets", EntityArgument.player())
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
									.then(
										net.minecraft.commands.Commands.argument("value", IntegerArgumentType.integer())
											.executes(
												context -> {
													ResourceLocation power = ResourceLocationArgument.getId(context, "power");
													ServerPlayer player = EntityArgument.getPlayer(context, "targets");
													PowerType powerType = CraftApoli.getPower(power.toString());
													int setTo = IntegerArgumentType.getInteger(context, "value");
													Component failure = Component.literal("No relevant score holders could be found");
													if (PowerHolderComponent.hasPower(player.getBukkitEntity(), powerType.getTag())
														&& PowerHolderComponent.isOfType(powerType, Resource.class)) {
														ResourcePower resourcePower = (ResourcePower) powerType;
														Resource.getDisplayedBar(player.getBukkitEntity(), resourcePower.getTag())
															.ifPresentOrElse(
																bar -> {
																	bar.change(setTo, "set");
																	context.getSource()
																		.sendSystemMessage(
																			Component.literal(
																				"Set {%} for {&} to ".replace("{%}", power.toString()).replace("{&}", player.displayName)
																					+ setTo
																			)
																		);
																},
																() -> context.getSource().sendFailure(failure)
															);
													} else {
														context.getSource().sendFailure(failure);
													}

													return 1;
												}
											)
									)
							)
					)
			)
			.then(
				net.minecraft.commands.Commands.literal("change")
					.then(
						net.minecraft.commands.Commands.argument("targets", EntityArgument.player())
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
									.then(
										net.minecraft.commands.Commands.argument("value", IntegerArgumentType.integer())
											.executes(
												context -> {
													ResourceLocation power = ResourceLocationArgument.getId(context, "power");
													ServerPlayer player = EntityArgument.getPlayer(context, "targets");
													PowerType powerType = CraftApoli.getPower(power.toString());
													int setTo = IntegerArgumentType.getInteger(context, "value");
													Component failure = Component.literal("No relevant score holders could be found");
													if (PowerHolderComponent.hasPower(player.getBukkitEntity(), powerType.getTag())
														&& PowerHolderComponent.isOfType(powerType, Resource.class)) {
														ResourcePower resourcePower = (ResourcePower) powerType;
														Resource.getDisplayedBar(player.getBukkitEntity(), resourcePower.getTag())
															.ifPresentOrElse(
																bar -> {
																	System.out.println(bar.getMappedProgress() + setTo);
																	bar.change(setTo, "add");
																	context.getSource()
																		.sendSystemMessage(
																			Component.literal(
																				"Set {%} for {&} to ".replace("{%}", power.toString()).replace("{&}", player.displayName) + setTo
																			)
																		);
																},
																() -> context.getSource().sendFailure(failure)
															);
													} else {
														context.getSource().sendFailure(failure);
													}

													return 1;
												}
											)
									)
							)
					)
			);
		dispatcher.register(main);*/
	}
}
