package me.dueris.genesismc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.content.OrbOfOrigins;
import me.dueris.genesismc.content.enchantment.AnvilHandler;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.apoli.RecipePower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.screen.ScreenConstants;
import me.dueris.genesismc.storage.OriginDataContainer;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.dueris.genesismc.storage.GenesisConfigs.getMainConfig;
import static me.dueris.genesismc.storage.GenesisConfigs.getOrbCon;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static org.bukkit.Bukkit.getServer;

public class OriginCommand extends BukkitRunnable implements Listener {

    public static final HashMap<Player, Integer> playerPage = new HashMap<>();
    public static EnumSet<Material> wearable;
    @SuppressWarnings("FieldMayBeFinal")
    public static HashMap<Player, ArrayList<Origin>> playerOrigins = new HashMap<>();
    public static List<Origin> commandProvidedOrigins = new ArrayList<>();
    public static List<Layer> commandProvidedLayers = new ArrayList<>();
    public static List<Power> commandProvidedPowers = new ArrayList<>();
    public static List<String> commandProvidedTaggedRecipies = new ArrayList<>();

    static {
        wearable = EnumSet.of(Material.ENCHANTED_BOOK, Material.BOOK, Material.PUMPKIN, Material.CARVED_PUMPKIN, Material.ELYTRA, Material.TURTLE_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_HELMET, Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_LEGGINGS, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS);
    }

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
                                    commandProvidedOrigins.forEach((origin) -> {
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
                                    targets.forEach(player -> {
                                        OriginPlayerAccessor.setOrigin(player.getBukkitEntity(), layer, origin);
                                        player.getBukkitEntity().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN, false);
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
                            if (!getMainConfig().getBoolean("orb-of-origins")) return 0;
                            @NotNull CraftInventoryCustom custommenu = (CraftInventoryCustom) Bukkit.createInventory(context.getSource().getPlayer().getBukkitEntity(), InventoryType.WORKBENCH, "Orb of Origins");
                            try {
                                CraftPlayer p = context.getSource().getPlayer().getBukkitEntity();
                                custommenu.setItem(1, new ItemStack(Material.valueOf(getOrbCon().get("crafting.top.left").toString())));
                                custommenu.setItem(2, new ItemStack(Material.valueOf(getOrbCon().get("crafting.top.middle").toString())));
                                custommenu.setItem(3, new ItemStack(Material.valueOf(getOrbCon().get("crafting.top.right").toString())));
                                custommenu.setItem(4, new ItemStack(Material.valueOf(getOrbCon().get("crafting.middle.left").toString())));
                                custommenu.setItem(5, new ItemStack(Material.valueOf(getOrbCon().get("crafting.middle.middle").toString())));
                                custommenu.setItem(6, new ItemStack(Material.valueOf(getOrbCon().get("crafting.middle.right").toString())));
                                custommenu.setItem(7, new ItemStack(Material.valueOf(getOrbCon().get("crafting.bottom.left").toString())));
                                custommenu.setItem(8, new ItemStack(Material.valueOf(getOrbCon().get("crafting.bottom.middle").toString())));
                                custommenu.setItem(9, new ItemStack(Material.valueOf(getOrbCon().get("crafting.bottom.right").toString())));
                                custommenu.setItem(0, OrbOfOrigins.orb);
                                p.openInventory(custommenu);
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
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
                                targets.forEach(player -> context.getSource().getBukkitEntity().sendMessage(net.kyori.adventure.text.Component.text("%player% has the following %layer% : %origin%".replace("%player%", player.getBukkitEntity().getName()).replace("%layer%", layer.getTag()).replace("%origin%", OriginPlayerAccessor.getOrigin(player.getBukkitEntity(), layer).getTag()))));
                                return SINGLE_SUCCESS;
                            })
                        )
                    )
                ).then(literal("gui").requires(source -> source.hasPermission(2))
                    .executes(context -> {
                        if (!context.getSource().isPlayer()) return 0;
                        OriginPlayerAccessor.unassignPowers(context.getSource().getPlayer().getBukkitEntity());
                        OriginPlayerAccessor.setOrigin(context.getSource().getPlayer().getBukkitEntity(), CraftApoli.getLayerFromTag("origins:origin"), CraftApoli.nullOrigin());
                        return SINGLE_SUCCESS;
                    })
                    .then(argument("targets", EntityArgument.players())
                        .executes(context -> {
                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                            targets.forEach(player -> {
                                for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                                    OriginPlayerAccessor.unassignPowers(player.getBukkitEntity());
                                    OriginPlayerAccessor.setOrigin(player.getBukkitEntity(), layer, CraftApoli.nullOrigin());
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
                                    OriginPlayerAccessor.unassignPowers(player.getBukkitEntity());
                                    OriginPlayerAccessor.setOrigin(player.getBukkitEntity(), layer, CraftApoli.nullOrigin());
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
                            assert origins != null;
                            playerOrigins.put(p.getBukkitEntity(), new ArrayList<>(origins.values()));
                            if (!playerPage.containsKey(p.getBukkitEntity()))
                                playerPage.put(p.getBukkitEntity(), 0);

                            @NotNull Inventory help = Bukkit.createInventory(p.getBukkitEntity(), 54, "Info - " + playerOrigins.get(p.getBukkitEntity()).get(playerPage.get(p.getBukkitEntity())).getName());
                            help.setContents(infoMenu(p.getBukkitEntity(), playerPage.get(p.getBukkitEntity())));
                            p.getBukkitEntity().openInventory(help);
                            p.getBukkitEntity().playSound(p.getBukkitEntity().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
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
                                commandProvidedTaggedRecipies.forEach((key) -> {
                                    builder.suggest(key);
                                });
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
                ).then(literal("enchant").requires(source -> source.hasPermission(2))
                    .then(argument("targets", EntityArgument.players())
                        .then(literal("origins:water_protection")
                            .executes(context -> {
                                enchant(context.getSource(), EntityArgument.getPlayers(context, "targets").stream().toList(), 1);
                                return SINGLE_SUCCESS;
                            })
                            .then(argument("level", IntegerArgumentType.integer(1, 4))
                                .executes(context -> {
                                    enchant(context.getSource(), EntityArgument.getPlayers(context, "targets").stream().toList(), IntegerArgumentType.getInteger(context, "level"));
                                    return SINGLE_SUCCESS;
                                })
                            )
                        )
                    )
                )
        );
    }

    public static ItemStack[] infoMenu(Player p, Integer page) {
        Origin origin = playerOrigins.get(p).get(page);

        ArrayList<Power> powerContainers = new ArrayList<>();
        for (Power powerContainer : origin.getPowerContainers()) {
            if (powerContainer.isHidden()) continue;
            powerContainers.add(powerContainer);
        }

        String minecraftItem = origin.getIcon();
        String item = null;
        if (minecraftItem.contains(":")) {
            item = minecraftItem.split(":")[1];
        } else {
            item = minecraftItem;
        }
        ItemStack originIcon = new ItemStack(Material.valueOf(item.toUpperCase()));

        ItemStack close = ScreenConstants.itemProperties(new ItemStack(Material.BARRIER), ChatColor.RED + "Close", null, null, null);
        ItemStack exit = ScreenConstants.itemProperties(new ItemStack(Material.SPECTRAL_ARROW), ChatColor.AQUA + "Close", ItemFlag.HIDE_ENCHANTS, null, null);
        ItemStack lowImpact = ScreenConstants.itemProperties(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.GREEN + "Low", null, null, null);
        ItemStack mediumImpact = ScreenConstants.itemProperties(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.YELLOW + "Medium", null, null, null);
        ItemStack highImpact = ScreenConstants.itemProperties(new ItemStack(Material.RED_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.RED + "High", null, null, null);
        ItemStack back = ScreenConstants.itemProperties(new ItemStack(Material.ARROW), "Back", ItemFlag.HIDE_ENCHANTS, null, null);
        ItemStack next = ScreenConstants.itemProperties(new ItemStack(Material.ARROW), "Next", ItemFlag.HIDE_ENCHANTS, null, null);


        ItemMeta originIconmeta = originIcon.getItemMeta();
        originIconmeta.setDisplayName(origin.getName());
        originIconmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        originIconmeta.setLore(ScreenConstants.cutStringIntoLines(origin.getDescription()));
        originIcon.setItemMeta(originIconmeta);

        NamespacedKey pageKey = new NamespacedKey(GenesisMC.getPlugin(), "page");
        ItemMeta backMeta = back.getItemMeta();
        if (page == 0) backMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, 0);
        else backMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, page - 1);
        back.setItemMeta(backMeta);


        ItemMeta nextMeta = next.getItemMeta();
        if (playerOrigins.get(p).size() - 1 == page)
            nextMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, page);
        else nextMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, page + 1);
        next.setItemMeta(nextMeta);


        ArrayList<ItemStack> contents = new ArrayList<>();
        long impact = origin.getImpact();

        for (int i = 0; i <= 53; i++) {
            if (i == 0 || i == 8) {
                contents.add(close);
            } else if (i == 1) {
                if (impact == 1) contents.add(lowImpact);
                else if (impact == 2) contents.add(mediumImpact);
                else if (impact == 3) contents.add(highImpact);
                else contents.add(new ItemStack(Material.AIR));
            } else if (i == 2) {
                if (impact == 2) contents.add(mediumImpact);
                else if (impact == 3) contents.add(highImpact);
                else contents.add(new ItemStack(Material.AIR));
            } else if (i == 3) {
                if (impact == 3) contents.add(highImpact);
                else contents.add(new ItemStack(Material.AIR));
            } else if (i == 4) {
                contents.add(OrbOfOrigins.orb);
            } else if (i == 5) {
                if (impact == 3) contents.add(highImpact);
                else contents.add(new ItemStack(Material.AIR));
            } else if (i == 6) {
                if (impact == 2) contents.add(mediumImpact);
                else if (impact == 3) contents.add(highImpact);
                else contents.add(new ItemStack(Material.AIR));
            } else if (i == 7) {
                if (impact == 1) contents.add(lowImpact);
                else if (impact == 2) contents.add(mediumImpact);
                else if (impact == 3) contents.add(highImpact);
                else contents.add(new ItemStack(Material.AIR));
            } else if (i == 13) {
                if (origin.getTag().equals("origins:human")) {
                    SkullMeta skull_p = (SkullMeta) originIcon.getItemMeta();
                    skull_p.setOwningPlayer(p);
                    skull_p.setOwner(p.getName());
                    skull_p.setPlayerProfile(p.getPlayerProfile());
                    skull_p.setOwnerProfile(p.getPlayerProfile());
                    originIcon.setItemMeta(skull_p);
                }
                contents.add(originIcon);
            } else if ((i >= 20 && i <= 24) || (i >= 29 && i <= 33) || (i >= 38 && i <= 42)) {
                while (powerContainers.size() > 0 && powerContainers.get(0).isHidden()) {
                    powerContainers.remove(0);
                }
                if (powerContainers.size() > 0) {

                    ItemStack originPower = new ItemStack(Material.FILLED_MAP);

                    ItemMeta meta = originPower.getItemMeta();
                    meta.setDisplayName(powerContainers.get(0).getName());
                    if (KeybindingUtils.renderKeybind(powerContainers.get(0)).getFirst()) {
                        meta.displayName(net.kyori.adventure.text.Component.text().append(meta.displayName()).append(net.kyori.adventure.text.Component.text(" ")).append(net.kyori.adventure.text.Component.text(KeybindingUtils.translateOriginRawKey(KeybindingUtils.renderKeybind(powerContainers.get(0)).getSecond())).color(TextColor.color(32222))).build());
                    }
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.setLore(ScreenConstants.cutStringIntoLines(powerContainers.get(0).getDescription()));
                    originPower.setItemMeta(meta);

                    contents.add(originPower);

                    powerContainers.remove(0);

                } else {
                    if (i >= 38) {
                        contents.add(new ItemStack(Material.AIR));
                    } else {
                        contents.add(new ItemStack(Material.PAPER));
                    }
                }


            } else if (i == 46) {
                contents.add(back);
            } else if (i == 49) {
                contents.add(exit);
            } else if (i == 52) {
                contents.add(next);
            } else {
                contents.add(new ItemStack(Material.AIR));
            }
        }
        return contents.toArray(new ItemStack[0]);
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

    public static void enchant(CommandSourceStack sender, List<ServerPlayer> targets, int level) {
        targets.removeIf(entity -> !(entity instanceof ServerPlayer));
        for (ServerPlayer entity : targets) {
            Player p = entity.getBukkitEntity();
            if (!OriginCommand.wearable.contains(p.getInventory().getItemInMainHand().getType())) {
                continue;
            }

            String romanLevel = AnvilHandler.numberToRomanNum(level);
            ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
            meta.setCustomModelData(level);
            p.getInventory().getItemInMainHand().setLore(List.of(ChatColor.GRAY + "Water Protection " + romanLevel));
            p.getInventory().getItemInMainHand().addEnchantment(AnvilHandler.bukkitEnchantment, level);
            sender.getBukkitSender().sendMessage("Applied enchantment " +
                ChatColor.GRAY + "{water_prot}".replace("{water_prot}", "Water Protection " + AnvilHandler.numberToRomanNum(level)) +
                ChatColor.WHITE + " to {target}'s item".replace("{target}", p.getName())
            );
        }
    }

    @EventHandler
    public void stopStealingInfo(InventoryClickEvent e) {
        if (e.getView().getTitle().startsWith("Info")) e.setCancelled(true);
    }

    @EventHandler
    public void onMenuExitInfo(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getView().getTitle().startsWith("Info")) {
            if (e.getCurrentItem().getType() == Material.BARRIER || e.getCurrentItem().getType() == Material.SPECTRAL_ARROW) {
                Player p = (Player) e.getWhoClicked();
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                e.getWhoClicked().getInventory().close();
            }
        }
    }

    @EventHandler
    public void onMenuScroll(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();
        if (item == null) return;
        if (!e.getView().getTitle().startsWith("Info")) return;
        if (item.getType() == Material.ARROW && (item.getItemMeta().getDisplayName().equals("Back") || item.getItemMeta().getDisplayName().equals("Next"))) {

            if (item.getItemMeta().getDisplayName().equals("Back") && playerPage.get(player) > 0)
                playerPage.put(player, playerPage.get(player) - 1);
            if (item.getItemMeta().getDisplayName().equals("Next") && playerPage.get(player) < playerOrigins.get(player).size() - 1)
                playerPage.put(player, playerPage.get(player) + 1);

            @NotNull Inventory info = Bukkit.createInventory(player, 54, "Info - " + playerOrigins.get(player).get(playerPage.get(player)).getName());
            info.setContents(infoMenu(player, item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "page"), PersistentDataType.INTEGER)));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
            player.closeInventory();
            player.openInventory(info);
        }
    }

    @EventHandler
    public void stopStealingRecipe(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("Orb of Origins") && e.getView().getTopInventory().getType().equals(InventoryType.WORKBENCH))
            e.setCancelled(true);
    }

    @EventHandler
    public void onMenuExitRecipe(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getView().getTitle().equalsIgnoreCase("Orb Recipe")) {
            if (e.getCurrentItem().getType() == Material.BARRIER) {
                Player p = (Player) e.getWhoClicked();
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                e.getWhoClicked().getInventory().close();
            }
        }
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