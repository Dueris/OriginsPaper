package me.dueris.originspaper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javassist.NotFoundException;
import me.dueris.originspaper.content.OrbOfOrigins;
import me.dueris.originspaper.event.OriginChangeEvent;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.factory.powers.apoli.RecipePower;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.registry.registries.Layer;
import me.dueris.originspaper.registry.registries.Origin;
import me.dueris.originspaper.screen.OriginPage;
import me.dueris.originspaper.screen.RandomOriginPage;
import me.dueris.originspaper.storage.OriginDataContainer;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.commands.CommandSourceStack;
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
import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.dueris.originspaper.storage.OriginConfiguration.getConfiguration;
import static me.dueris.originspaper.storage.OriginConfiguration.getOrbConfiguration;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static org.bukkit.Bukkit.getServer;

public class OriginCommand extends BukkitRunnable implements Listener {

	public static final HashMap<Player, Integer> playerPage = new HashMap<>();
	@SuppressWarnings("FieldMayBeFinal")
	public static HashMap<Player, ArrayList<Origin>> playerOrigins = new HashMap<>();
	public static List<Origin> commandProvidedOrigins = new ArrayList<>();
	public static List<Layer> commandProvidedLayers = new ArrayList<>();
	public static List<PowerType> commandProvidedPowers = new ArrayList<>();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			literal("origin")
				.then(literal("set").requires(source -> source.hasPermission(2))
					.then(argument("targets", EntityArgument.players())
						.then(argument("layer", ResourceLocationArgument.id())
							.suggests((context, builder) -> {
								commandProvidedLayers.forEach((layer) -> {
									if (context.getInput().split(" ").length == 3 || (layer.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
										|| layer.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))) {
										builder.suggest(layer.getTag());
									}
								});
								return builder.buildFuture();
							})
							.then(argument("origin", ResourceLocationArgument.id())
								.suggests((context, builder) -> {
									Layer layer = CraftApoli.getLayerFromTag(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString());
									commandProvidedOrigins.stream().filter(o -> layer.getOriginIdentifiers().contains(o.getTag())).forEach((origin) -> {
										if (context.getInput().split(" ").length == 4 || (origin.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
											|| origin.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))) {
											builder.suggest(origin.getTag());
										}
									});
									return builder.buildFuture();
								}).executes(context -> {
									Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
									Layer layer = CraftApoli.getLayerFromTag(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString());
									Origin origin = CraftApoli.getOrigin(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "origin")).asString());
									if (!layer.getOriginIdentifiers().contains(origin.getTag())) {
										context.getSource().sendFailure(Component.literal("Origin \"%e%\" not found on layer: "
											.replace("%e%", origin.getTag())
											+ CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString()));
										return 0;
									}
									targets.forEach(player -> {
										PowerHolderComponent.setOrigin(player.getBukkitEntity(), layer, origin);
										player.getBukkitEntity().getPersistentDataContainer().set(CraftNamespacedKey.fromMinecraft(ResourceLocation.fromNamespaceAndPath("originspaper", "in-phantomform")), PersistentDataType.BOOLEAN, false);
										OriginChangeEvent originChangeEvent = new OriginChangeEvent(player.getBukkitEntity(), origin, false);
										getServer().getPluginManager().callEvent(originChangeEvent);
									});
									return SINGLE_SUCCESS;
								})
							)
						))
				).then(literal("recipe")
					.executes(context -> {
						if (context.getSource().isPlayer()) {
							if (!getConfiguration().getBoolean("orb-of-origins")) return 0;
							@NotNull CraftInventoryCustom custommenu = (CraftInventoryCustom) Bukkit.createInventory(context.getSource().getPlayer().getBukkitEntity(), InventoryType.WORKBENCH, "Orb of Origins");
							try {
								CraftPlayer p = context.getSource().getPlayer().getBukkitEntity();
								custommenu.setItem(1, new ItemStack(Material.valueOf(getOrbConfiguration().get("crafting.top.left").toString())));
								custommenu.setItem(2, new ItemStack(Material.valueOf(getOrbConfiguration().get("crafting.top.middle").toString())));
								custommenu.setItem(3, new ItemStack(Material.valueOf(getOrbConfiguration().get("crafting.top.right").toString())));
								custommenu.setItem(4, new ItemStack(Material.valueOf(getOrbConfiguration().get("crafting.middle.left").toString())));
								custommenu.setItem(5, new ItemStack(Material.valueOf(getOrbConfiguration().get("crafting.middle.middle").toString())));
								custommenu.setItem(6, new ItemStack(Material.valueOf(getOrbConfiguration().get("crafting.middle.right").toString())));
								custommenu.setItem(7, new ItemStack(Material.valueOf(getOrbConfiguration().get("crafting.bottom.left").toString())));
								custommenu.setItem(8, new ItemStack(Material.valueOf(getOrbConfiguration().get("crafting.bottom.middle").toString())));
								custommenu.setItem(9, new ItemStack(Material.valueOf(getOrbConfiguration().get("crafting.bottom.right").toString())));
								custommenu.setItem(0, OrbOfOrigins.orb);
								p.openInventory(custommenu);
								p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 10, 9);
							} catch (Exception exception) {
								exception.printStackTrace();
							}
							return SINGLE_SUCCESS;
						} else {
							context.getSource().sendFailure(Component.literal("Only players can access this command"));
							return 0;
						}

					})
				).then(literal("get").requires(source -> source.hasPermission(2))
					.then(argument("targets", EntityArgument.players())
						.then(argument("layer", ResourceLocationArgument.id())
							.suggests((context, builder) -> {
								commandProvidedLayers.forEach((layer) -> {
									if (context.getInput().split(" ").length == 3 || (layer.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
										|| layer.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))) {
										builder.suggest(layer.getTag());
									}
								});
								return builder.buildFuture();
							}).executes(context -> {
								Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
								Layer layer = CraftApoli.getLayerFromTag(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString());
								targets.forEach(player -> context.getSource().getBukkitEntity().sendMessage(net.kyori.adventure.text.Component.text("%player% has the following %layer% : %origin%".replace("%player%", player.getBukkitEntity().getName()).replace("%layer%", layer.getTag()).replace("%origin%", PowerHolderComponent.getOrigin(player.getBukkitEntity(), layer).getTag()))));
								return SINGLE_SUCCESS;
							})
						)
					)
				).then(literal("random").requires(source -> source.hasPermission(2))
					.executes(context -> {
						if (!context.getSource().isPlayer()) return 0;
						ServerPlayer player = context.getSource().getPlayer();
						RandomOriginPage randomOriginPage = new RandomOriginPage();
						CraftApoli.getLayersFromRegistry().forEach(layer -> randomOriginPage.onChoose(player, layer));
						return SINGLE_SUCCESS;
					})
					.then(argument("targets", EntityArgument.players())
						.executes(context -> {
							Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
							RandomOriginPage randomOriginPage = new RandomOriginPage();
							targets.forEach(player -> CraftApoli.getLayersFromRegistry().forEach(layer -> randomOriginPage.onChoose(player, layer)));
							return SINGLE_SUCCESS;
						})
						.then(argument("layer", ResourceLocationArgument.id())
							.suggests((context, builder) -> {
								commandProvidedLayers.forEach((layer) -> {
									if (context.getInput().split(" ").length == 3 || (layer.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
										|| layer.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))) {
										builder.suggest(layer.getTag());
									}
								});
								return builder.buildFuture();
							}).executes(context -> {
								Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
								Layer layer = CraftApoli.getLayerFromTag(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString());
								RandomOriginPage randomOriginPage = new RandomOriginPage();
								targets.forEach(player -> randomOriginPage.onChoose(player, layer));
								return SINGLE_SUCCESS;
							})
						)
					)
				).then(literal("gui").requires(source -> source.hasPermission(2))
					.executes(context -> {
						if (!context.getSource().isPlayer()) return 0;
						ServerPlayer player = context.getSource().getPlayer();
						CraftApoli.getLayersFromRegistry().forEach(layer -> {
							try {
								PowerHolderComponent.unassignPowers(player.getBukkitEntity(), layer);
							} catch (NotFoundException e) {
								throw new RuntimeException(e);
							}
							PowerHolderComponent.setOrigin(player.getBukkitEntity(), layer, CraftApoli.emptyOrigin());
						});
						return SINGLE_SUCCESS;
					})
					.then(argument("targets", EntityArgument.players())
						.executes(context -> {
							Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
							targets.forEach(player -> {
								for (Layer layer : CraftApoli.getLayersFromRegistry()) {
									try {
										PowerHolderComponent.unassignPowers(player.getBukkitEntity(), layer);
									} catch (NotFoundException e) {
										throw new RuntimeException(e);
									}
									PowerHolderComponent.setOrigin(player.getBukkitEntity(), layer, CraftApoli.emptyOrigin());
								}
							});
							return SINGLE_SUCCESS;
						})
						.then(argument("layer", ResourceLocationArgument.id())
							.suggests((context, builder) -> {
								commandProvidedLayers.forEach((layer) -> {
									if (context.getInput().split(" ").length == 3 || (layer.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
										|| layer.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))) {
										builder.suggest(layer.getTag());
									}
								});
								return builder.buildFuture();
							}).executes(context -> {
								Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
								Layer layer = CraftApoli.getLayerFromTag(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString());
								targets.forEach(player -> {
									try {
										PowerHolderComponent.unassignPowers(player.getBukkitEntity(), layer);
									} catch (NotFoundException e) {
										throw new RuntimeException(e);
									}
									PowerHolderComponent.setOrigin(player.getBukkitEntity(), layer, CraftApoli.emptyOrigin());
								});
								return SINGLE_SUCCESS;
							})
						)
					)
				).then(literal("info")
					.executes(context -> {
						if (context.getSource().isPlayer()) {
							ServerPlayer p = context.getSource().getPlayer();
							HashMap<Layer, Origin> origins = CraftApoli.toOrigin(OriginDataContainer.getLayer(p.getBukkitEntity()));
							origins.entrySet().removeIf(entry -> entry.getKey().isHidden());
							playerOrigins.put(p.getBukkitEntity(), new ArrayList<>(origins.values()));
							if (!playerPage.containsKey(p.getBukkitEntity()))
								playerPage.put(p.getBukkitEntity(), 0);

							@NotNull Inventory help = Bukkit.createInventory(p.getBukkitEntity(), 54, "Info - " + playerOrigins.get(p.getBukkitEntity()).get(playerPage.get(p.getBukkitEntity())).getName());
							help.setContents(new OriginPage(playerOrigins.get(p.getBukkitEntity()).get(playerPage.get(p.getBukkitEntity()))).createDisplay(p, null));
							p.getBukkitEntity().openInventory(help);
							p.getBukkitEntity().playSound(p.getBukkitEntity().getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
							return SINGLE_SUCCESS;
						} else {
							context.getSource().sendFailure(Component.literal("Only players can access this command"));
							return 0;
						}
					})
				).then(literal("give").requires(source -> source.hasPermission(2))
					.then(argument("targets", EntityArgument.players())
						.then(argument("namespace", ResourceLocationArgument.id())
							.suggests((context, builder) -> {
								RecipePower.tags.forEach(builder::suggest);
								builder.suggest("origins:orb_of_origins");
								return builder.buildFuture();
							})
							.executes(context -> {
								give(context, 1);
								return SINGLE_SUCCESS;
							})
							.then(argument("amount", IntegerArgumentType.integer())
								.executes(context -> {
									give(context, IntegerArgumentType.getInteger(context, "amount"));
									return SINGLE_SUCCESS;
								})
							)
						)
					)
				)
		);
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
		if (e.getView().getTitle().startsWith("Info")) e.setCancelled(true);
	}

	@EventHandler
	public void onMenuScroll(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Player player = (Player) e.getWhoClicked();
		if (item == null) return;
		if (!e.getView().getTitle().startsWith("Info")) return;
		if (item.getType() == Material.ARROW && (item.getItemMeta().getDisplayName().equals("Back Origin") || item.getItemMeta().getDisplayName().equals("Next Origin"))) {
			if (item.getItemMeta().getDisplayName().equals("Back Origin") && playerPage.get(player) > 0)
				playerPage.put(player, playerPage.get(player) - 1);
			if (item.getItemMeta().getDisplayName().equals("Next Origin") && playerPage.get(player) < playerOrigins.get(player).size() - 1)
				playerPage.put(player, playerPage.get(player) + 1);

			@NotNull Inventory info = Bukkit.createInventory(player, 54, "Info - " + playerOrigins.get(player).get(playerPage.get(player)).getName());
			info.setContents(new OriginPage(playerOrigins.get(player).get(playerPage.get(player))).createDisplay(((CraftPlayer) player).getHandle(), null));
			player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
			player.closeInventory();
			player.openInventory(info);
		}
	}

	@EventHandler
	public void stopStealingRecipe(InventoryClickEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase("Orb of Origins") && e.getView().getTopInventory().getType().equals(InventoryType.WORKBENCH))
			e.setCancelled(true);
	}

	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.getOpenInventory().getTitle().startsWith("Info")) {
				playerPage.remove(player);
				playerOrigins.remove(player);
			}
		}
	}

}