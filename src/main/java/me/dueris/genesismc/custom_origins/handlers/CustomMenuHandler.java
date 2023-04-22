package me.dueris.genesismc.custom_origins.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.Material.*;

public class CustomMenuHandler implements Listener {

    @EventHandler
    public static void onClickCustomOpen(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof HumanEntity){
            Player p = (Player) e.getWhoClicked();
            File custom_folder = new File(getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");
            if(custom_folder.listFiles().length >= 1) {
                DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
                    @Override
                    public boolean accept(Path file) throws IOException {
                        return (Files.isDirectory(file));
                    }
                };
                if (e.getView().getTitle().equalsIgnoreCase(LIGHT_PURPLE + "Origins Menu")) {
                      if (p.hasPermission("genesismc.choosing.custom.canchoose")) {
                      if (e.getCurrentItem() != null) {
                      if (e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
                      if (e.getView().getTitle().equalsIgnoreCase(LIGHT_PURPLE + "Origins Menu")) {
                                                                if (e.getCurrentItem() != null) {
                                                                    if (e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
                                                                            Inventory customOriginsMenu = Bukkit.createInventory(p, 27, "Custom Origins");
                                                                            ItemStack bar_wall = new ItemStack(Material.IRON_BARS);
                                                                            ItemStack empty_slot = new ItemStack(Material.RED_STAINED_GLASS_PANE);


                                                                            ItemStack[] custom_items = {bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall, orb, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, orb, bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall};
                                                                            customOriginsMenu.setContents(custom_items);
                                                                            p.openInventory(customOriginsMenu);
                                                                    }
                                                                }
                                                            }
                                                    }
                                                }
                                            }
                                        }

                                        if(e.getView().getTitle().equalsIgnoreCase("Custom Origins")){
                                            if(e.isLeftClick() || e.isRightClick() || e.isShiftClick()){
                                                e.setCancelled(true);
                                            }
                                        }
                        }else{
                            if (e.getView().getTitle().equalsIgnoreCase(LIGHT_PURPLE + "Origins Menu")) {
                                if (e.getCurrentItem() != null) {
                                    if (e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
                                        if (custom_folder.listFiles().length <= 21) {
                                            Inventory customOriginsMenu = Bukkit.createInventory(p, 18, "Custom Origins");
                                            ItemStack bar_wall = new ItemStack(Material.IRON_BARS);
                                            ItemStack empty_slot = new ItemStack(Material.RED_STAINED_GLASS_PANE);


                                            ItemStack[] custom_items = {bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall, orb, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, orb, bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall};
                                            customOriginsMenu.setContents(custom_items);
                                            p.openInventory(customOriginsMenu);
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
    }
