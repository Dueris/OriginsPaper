package me.dueris.originspaper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.dueris.calio.registry.Registrar;
import io.github.dueris.calio.registry.impl.CalioRegistry;
import javassist.NotFoundException;
import me.dueris.originspaper.CraftApoli;
import me.dueris.originspaper.content.OrbOfOrigins;
import me.dueris.originspaper.event.OriginChangeEvent;
import me.dueris.originspaper.power.RecipePower;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.Origin;
import me.dueris.originspaper.registry.registries.OriginLayer;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.screen.OriginPage;
import me.dueris.originspaper.screen.RandomOriginPage;
import me.dueris.originspaper.storage.OriginConfiguration;
import me.dueris.originspaper.storage.OriginDataContainer;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class OriginCommand extends BukkitRunnable implements Listener {
	public static final HashMap<Player, Integer> playerPage = new HashMap<>();
	public static HashMap<Player, ArrayList<Origin>> playerOrigins = new HashMap<>();
	protected static Registrar<OriginLayer> LAYERS = CalioRegistry.INSTANCE.retrieve(Registries.LAYER);
	protected static Registrar<Origin> ORIGINS = CalioRegistry.INSTANCE.retrieve(Registries.ORIGIN);
	protected static Registrar<PowerType> POWERS = CalioRegistry.INSTANCE.retrieve(Registries.CRAFT_POWER);

	public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			Commands.literal("origin")
				.then(
					(Commands.literal("set").requires(source -> source.hasPermission(2)))
						.then(
							Commands.argument("targets", EntityArgument.players())
								.then(
									Commands.argument("layer", ResourceLocationArgument.id())
										.suggests(
											(context, builder) -> {
												LAYERS.forEach(
													(location, layer) -> {
														if (context.getInput().split(" ").length == 3
															|| layer.getTag()
															.startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
															|| layer.getTag().split(":")[1]
															.startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])) {
															builder.suggest(layer.getTag());
														}
													}
												);
												return builder.buildFuture();
											}
										)
										.then(
											Commands.argument("origin", ResourceLocationArgument.id())
												.suggests(
													(context, builder) -> {
														OriginLayer layer = CraftApoli.getLayer(ResourceLocationArgument.getId(context, "layer"));
														layer.getOriginIdentifiers().stream().map(ResourceLocation::toString).filter(tag -> {
															String input = context.getInput().split(" ")[context.getInput().split(" ").length - 1];
															return (tag.startsWith(input)) || context.getInput().split(" ").length == 4;
														}).forEach(builder::suggest);
														return builder.buildFuture();
													}
												)
												.executes(
													context -> {
														Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
														OriginLayer layer = CraftApoli.getLayer(
															CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString()
														);
														Origin origin = CraftApoli.getOrigin(
															CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "origin")).asString()
														);
														if (!layer.getOriginIdentifiers().contains(ResourceLocation.parse(origin.getTag()))) {
															context.getSource()
																.sendFailure(
																	Component.literal(
																		"Origin \"%e%\" not found on layer: ".replace("%e%", origin.getTag())
																			+ CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer"))
																			.asString()
																	)
																);
															return 0;
														} else {
															targets.forEach(
																player -> {
																	PowerHolderComponent.setOrigin(player.getBukkitEntity(), layer, origin);
																	player.getBukkitEntity()
																		.getPersistentDataContainer()
																		.set(
																			CraftNamespacedKey.fromMinecraft(
																				ResourceLocation.fromNamespaceAndPath("originspaper", "in-phantomform")
																			),
																			PersistentDataType.BOOLEAN,
																			false
																		);
																	OriginChangeEvent originChangeEvent = new OriginChangeEvent(
																		player.getBukkitEntity(), origin, false
																	);
																	Bukkit.getServer().getPluginManager().callEvent(originChangeEvent);
																}
															);
															return 1;
														}
													}
												)
										)
								)
						)
				)
				.then(
					Commands.literal("recipe")
						.executes(
							context -> {
								if (context.getSource().isPlayer()) {
									if (!OriginConfiguration.getConfiguration().getBoolean("orb-of-origins")) {
										return 0;
									} else {
										CraftInventoryCustom custommenu = (CraftInventoryCustom) Bukkit.createInventory(
											context.getSource().getPlayer().getBukkitEntity(),
											InventoryType.WORKBENCH,
											"Orb of Origins"
										);

										try {
											CraftPlayer p = context.getSource().getPlayer().getBukkitEntity();
											custommenu.setItem(
												1,
												new ItemStack(
													Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.top.left").toString())
												)
											);
											custommenu.setItem(
												2,
												new ItemStack(
													Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.top.middle").toString())
												)
											);
											custommenu.setItem(
												3,
												new ItemStack(
													Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.top.right").toString())
												)
											);
											custommenu.setItem(
												4,
												new ItemStack(
													Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.middle.left").toString())
												)
											);
											custommenu.setItem(
												5,
												new ItemStack(
													Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.middle.middle").toString())
												)
											);
											custommenu.setItem(
												6,
												new ItemStack(
													Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.middle.right").toString())
												)
											);
											custommenu.setItem(
												7,
												new ItemStack(
													Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.bottom.left").toString())
												)
											);
											custommenu.setItem(
												8,
												new ItemStack(
													Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.bottom.middle").toString())
												)
											);
											custommenu.setItem(
												9,
												new ItemStack(
													Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.bottom.right").toString())
												)
											);
											custommenu.setItem(0, OrbOfOrigins.orb);
											p.openInventory(custommenu);
											p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 10.0F, 9.0F);
										} catch (Exception var3) {
											var3.printStackTrace();
										}

										return 1;
									}
								} else {
									context.getSource().sendFailure(Component.literal("Only players can access this command"));
									return 0;
								}
							}
						)
				)
				.then(
					(Commands.literal("get").requires(source -> source.hasPermission(2)))
						.then(
							Commands.argument("targets", EntityArgument.players())
								.then(
									Commands.argument("layer", ResourceLocationArgument.id())
										.suggests(
											(context, builder) -> {
												LAYERS.forEach(
													(location, layer) -> {
														if (context.getInput().split(" ").length == 3
															|| layer.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
															|| layer.getTag().split(":")[1]
															.startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])) {
															builder.suggest(layer.getTag());
														}
													}
												);
												return builder.buildFuture();
											}
										)
										.executes(
											context -> {
												Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
												OriginLayer layer = CraftApoli.getLayer(
													CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString()
												);
												targets.forEach(
													player -> context.getSource()
														.sendSystemMessage(
															Component.literal(
																"%player% has the following %layer% : %origin%"
																	.replace("%player%", player.getBukkitEntity().getName())
																	.replace("%layer%", layer.getTag())
																	.replace("%origin%", PowerHolderComponent.getOrigin(player.getBukkitEntity(), layer).getTag())
															)
														)
												);
												return 1;
											}
										)
								)
						)
				)
				.then(
					(Commands.literal("random")
						.requires(source -> source.hasPermission(2)))
						.executes(context -> {
							if (!context.getSource().isPlayer()) {
								return 0;
							} else {
								ServerPlayer player = context.getSource().getPlayer();
								RandomOriginPage randomOriginPage = new RandomOriginPage();
								CraftApoli.getLayersFromRegistry().forEach(layer -> randomOriginPage.onChoose(player, layer));
								return 1;
							}
						})
						.then(
							(Commands.argument("targets", EntityArgument.players()).executes(context -> {
								Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
								RandomOriginPage randomOriginPage = new RandomOriginPage();
								targets.forEach(player -> CraftApoli.getLayersFromRegistry().forEach(layer -> randomOriginPage.onChoose(player, layer)));
								return 1;
							}))
								.then(
									Commands.argument("layer", ResourceLocationArgument.id())
										.suggests(
											(context, builder) -> {
												LAYERS.forEach(
													(location, layer) -> {
														if (context.getInput().split(" ").length == 3
															|| layer.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
															|| layer.getTag().split(":")[1]
															.startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])) {
															builder.suggest(layer.getTag());
														}
													}
												);
												return builder.buildFuture();
											}
										)
										.executes(context -> {
											Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
											OriginLayer layer = CraftApoli.getLayer(
												CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString()
											);
											RandomOriginPage randomOriginPage = new RandomOriginPage();
											targets.forEach(player -> randomOriginPage.onChoose(player, layer));
											return 1;
										})
								)
						)
				)
				.then(
					((Commands.literal("gui")
						.requires(source -> source.hasPermission(2)))
						.executes(context -> {
							if (!context.getSource().isPlayer()) {
								return 0;
							} else {
								ServerPlayer player = context.getSource().getPlayer();
								CraftApoli.getLayersFromRegistry().forEach(layer -> {
									try {
										PowerHolderComponent.unassignPowers(player.getBukkitEntity(), layer);
									} catch (NotFoundException var3) {
										throw new RuntimeException(var3);
									}

									PowerHolderComponent.setOrigin(player.getBukkitEntity(), layer, CraftApoli.emptyOrigin());
								});
								return 1;
							}
						})
						.then(
							(Commands.argument("targets", EntityArgument.players()).executes(context -> {
								Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
								targets.forEach(player -> {
									for (OriginLayer layer : CraftApoli.getLayersFromRegistry()) {
										try {
											PowerHolderComponent.unassignPowers(player.getBukkitEntity(), layer);
										} catch (NotFoundException var4) {
											throw new RuntimeException(var4);
										}

										PowerHolderComponent.setOrigin(player.getBukkitEntity(), layer, CraftApoli.emptyOrigin());
									}
								});
								return 1;
							}))
								.then(
									Commands.argument("layer", ResourceLocationArgument.id())
										.suggests(
											(context, builder) -> {
												LAYERS.forEach(
													(location, layer) -> {
														if (context.getInput().split(" ").length == 3
															|| layer.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
															|| layer.getTag().split(":")[1]
															.startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])) {
															builder.suggest(layer.getTag());
														}
													}
												);
												return builder.buildFuture();
											}
										)
										.executes(context -> {
											Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
											OriginLayer layer = CraftApoli.getLayer(
												CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString()
											);
											targets.forEach(player -> {
												try {
													PowerHolderComponent.unassignPowers(player.getBukkitEntity(), layer);
												} catch (NotFoundException var3) {
													throw new RuntimeException(var3);
												}

												PowerHolderComponent.setOrigin(player.getBukkitEntity(), layer, CraftApoli.emptyOrigin());
											});
											return 1;
										})
								)
						)
					)
				).then(
					Commands.literal("info")
						.executes(
							context -> {
								if (context.getSource().isPlayer()) {
									ServerPlayer p = context.getSource().getPlayer();
									HashMap<OriginLayer, Origin> origins = CraftApoli.toOrigin(OriginDataContainer.getLayer(p.getBukkitEntity()));
									origins.entrySet().removeIf(entry -> entry.getKey().isHidden());
									playerOrigins.put(p.getBukkitEntity(), new ArrayList<>(origins.values()));
									if (!playerPage.containsKey(p.getBukkitEntity())) {
										playerPage.put(p.getBukkitEntity(), 0);
									}

									Inventory help = Bukkit.createInventory(
										p.getBukkitEntity(), 54, net.kyori.adventure.text.Component.text("Info - ").append(playerOrigins.get(p.getBukkitEntity()).get(playerPage.get(p.getBukkitEntity())).name())
									);
									help.setContents(
										new OriginPage(playerOrigins.get(p.getBukkitEntity()).get(playerPage.get(p.getBukkitEntity()))).createDisplay(p, null)
									);
									p.getBukkitEntity().openInventory(help);
									p.getBukkitEntity().playSound(p.getBukkitEntity().getLocation(), Sound.UI_BUTTON_CLICK, 2.0F, 1.0F);
									return 1;
								} else {
									context.getSource().sendFailure(Component.literal("Only players can access this command"));
									return 0;
								}
							}
						)
				)
				.then(
					(Commands.literal("give").requires(source -> source.hasPermission(2)))
						.then(
							Commands.argument("targets", EntityArgument.players())
								.then(
									(Commands.argument("namespace", ResourceLocationArgument.id())
										.suggests((context, builder) -> {
											RecipePower.tags.forEach(builder::suggest);
											builder.suggest("origins:orb_of_origins");
											return builder.buildFuture();
										})
										.executes(context -> {
											give(context, 1);
											return 1;
										}))
										.then(Commands.argument("amount", IntegerArgumentType.integer()).executes(context -> {
											give(context, IntegerArgumentType.getInteger(context, "amount"));
											return 1;
										}))
								)
						)
				));
	}

	public static void give(CommandContext<CommandSourceStack> context, int amt) throws CommandSyntaxException {
		String tag = CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "namespace")).asString();
		if (RecipePower.taggedRegistry.containsKey(tag)) {
			for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
				Recipe recipe = RecipePower.taggedRegistry.get(tag);
				ItemStack itemStack = recipe.getResult().clone();
				itemStack.setAmount(amt);
				player.addItem(CraftItemStack.asNMSCopy(itemStack));
			}
		} else {
			context.getSource().sendFailure(Component.literal("Item not found in origins registry."));
		}
	}

	@EventHandler
	public void stopStealingInfo(InventoryClickEvent e) {
		if (e.getView().getTitle().startsWith("Info")) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onMenuScroll(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Player player = (Player) e.getWhoClicked();
		if (item != null) {
			if (e.getView().getTitle().startsWith("Info")) {
				if (item.getType() == Material.ARROW
					&& (item.getItemMeta().getDisplayName().equals("Back Origin") || item.getItemMeta().getDisplayName().equals("Next Origin"))) {
					if (item.getItemMeta().getDisplayName().equals("Back Origin") && playerPage.get(player) > 0) {
						playerPage.put(player, playerPage.get(player) - 1);
					}

					if (item.getItemMeta().getDisplayName().equals("Next Origin") && playerPage.get(player) < playerOrigins.get(player).size() - 1) {
						playerPage.put(player, playerPage.get(player) + 1);
					}

					Inventory info = Bukkit.createInventory(player, 54, net.kyori.adventure.text.Component.text("Info - ").append(playerOrigins.get(player).get(playerPage.get(player)).name()));
					info.setContents(new OriginPage(playerOrigins.get(player).get(playerPage.get(player))).createDisplay(((CraftPlayer) player).getHandle(), null));
					player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2.0F, 1.0F);
					player.closeInventory();
					player.openInventory(info);
				}
			}
		}
	}

	@EventHandler
	public void stopStealingRecipe(InventoryClickEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase("Orb of Origins") && e.getView().getTopInventory().getType().equals(InventoryType.WORKBENCH)) {
			e.setCancelled(true);
		}
	}

	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.getOpenInventory().getTitle().startsWith("Info")) {
				playerPage.remove(player);
				playerOrigins.remove(player);
			}
		}
	}
}
