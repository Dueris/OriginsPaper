package me.dueris.genesismc.core;

import me.dueris.genesismc.core.utils.ShulkUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.util.ArrayList;

import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.RED;

public class KeybindHandler implements Listener {

    @EventHandler
    public void OnPressMainKey(PlayerSwapHandItemsEvent e) {
        Player p = (Player) e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Geyser-Spigot")) {
            if (!GeyserApi.api().isBedrockPlayer(p.getUniqueId())) {
                //geyser enabled. run code
                if (originid == 6503044) {
                    e.setCancelled(true);

                    ArrayList<ItemStack> vaultItems = ShulkUtils.getItems(p);

                    Inventory vault = Bukkit.createInventory(p, InventoryType.DROPPER, "Shulker Inventory");

                    vaultItems.stream()
                            .forEach(itemStack -> vault.addItem(itemStack));

                    p.openInventory(vault);

                } else if (originid == 7300041) {
                    e.setCancelled(true);
                    int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
                    if (phantomid == 1) {
                        if (p.getGameMode() != GameMode.SPECTATOR) {

                            if (p.getFoodLevel() > 6) {
                                p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 2);
                                p.sendActionBar(DARK_AQUA + "Activated Phantom Form");
                                p.setInvisible(true);
                                p.setSilent(true);
                                p.setCollidable(false);
                                p.setInvulnerable(false);
                                p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.8);

                            } else {
                                p.sendMessage(RED + "You must be able to sprint to switch forms");
                            }

                            CraftPlayer craftPlayer = (CraftPlayer) p;

                        } else {
                            p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");
                        }
                    } else if (phantomid == 2) {
                        if (p.getGameMode() != GameMode.SPECTATOR) {

                            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                            p.sendActionBar(DARK_AQUA + "Deactivated Phantom Form");
                            p.setInvisible(false);
                            p.setSilent(false);
                            p.setCollidable(true);
                            p.setInvulnerable(false);
                            p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.11);

                        } else {
                            p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");
                        }
                    } else {
                        p.sendMessage(RED + "Error: Switching could not be executed");
                    }
                }
                if(Bukkit.getServer().getPluginManager().isPluginEnabled("floodgate")) {
                    //floodgate enabled
                    if (!FloodgateApi.getInstance().isFloodgatePlayer(p.getUniqueId())) {

                        if (originid == 6503044) {
                            e.setCancelled(true);

                            ArrayList<ItemStack> vaultItems = ShulkUtils.getItems(p);

                            Inventory vault = Bukkit.createInventory(p, InventoryType.DROPPER, "Shulker Inventory");

                            vaultItems.stream()
                                    .forEach(itemStack -> vault.addItem(itemStack));

                            p.openInventory(vault);

                        } else if (originid == 7300041) {
                            e.setCancelled(true);
                            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
                            if (phantomid == 1) {
                                if (p.getGameMode() != GameMode.SPECTATOR) {

                                    if (p.getFoodLevel() > 6) {
                                        p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 2);
                                        p.sendActionBar(DARK_AQUA + "Activated Phantom Form");
                                        p.setInvisible(true);
                                        p.setSilent(true);
                                        p.setCollidable(false);
                                        p.setInvulnerable(false);
                                        p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.8);

                                    } else {
                                        p.sendMessage(RED + "You must be able to sprint to switch forms");
                                    }

                                    CraftPlayer craftPlayer = (CraftPlayer) p;

                                } else {
                                    p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");
                                }
                            } else if (phantomid == 2) {
                                if (p.getGameMode() != GameMode.SPECTATOR) {

                                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                                    p.sendActionBar(DARK_AQUA + "Deactivated Phantom Form");
                                    p.setInvisible(false);
                                    p.setSilent(false);
                                    p.setCollidable(true);
                                    p.setInvulnerable(false);
                                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.11);

                                } else {
                                    p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");
                                }
                            } else {
                                p.sendMessage(RED + "Error: Switching could not be executed");
                            }
                        }


                    }
                }


            }
        }else{
            //geyser not on server
            if (originid == 6503044) {
                e.setCancelled(true);

                ArrayList<ItemStack> vaultItems = ShulkUtils.getItems(p);

                Inventory vault = Bukkit.createInventory(p, InventoryType.DROPPER, "Shulker Inventory");

                vaultItems.stream()
                        .forEach(itemStack -> vault.addItem(itemStack));

                p.openInventory(vault);

            } else if (originid == 7300041) {
                e.setCancelled(true);
                int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
                if (phantomid == 1) {
                    if (p.getGameMode() != GameMode.SPECTATOR) {

                        if (p.getFoodLevel() > 6) {
                            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 2);
                            p.sendActionBar(DARK_AQUA + "Activated Phantom Form");
                            p.setInvisible(true);
                            p.setSilent(true);
                            p.setCollidable(false);
                            p.setInvulnerable(false);
                            p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.8);

                        } else {
                            p.sendMessage(RED + "You must be able to sprint to switch forms");
                        }

                        CraftPlayer craftPlayer = (CraftPlayer) p;

                    } else {
                        p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");
                    }
                } else if (phantomid == 2) {
                    if (p.getGameMode() != GameMode.SPECTATOR) {

                        p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                        p.sendActionBar(DARK_AQUA + "Deactivated Phantom Form");
                        p.setInvisible(false);
                        p.setSilent(false);
                        p.setCollidable(true);
                        p.setInvulnerable(false);
                        p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.11);

                    } else {
                        p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");
                    }
                } else {
                    p.sendMessage(RED + "Error: Switching could not be executed");
                }
            }
        }
    }


}
