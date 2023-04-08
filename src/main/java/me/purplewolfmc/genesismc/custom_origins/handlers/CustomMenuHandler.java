package me.purplewolfmc.genesismc.custom_origins.handlers;

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

import static me.purplewolfmc.genesismc.core.items.OrbOfOrigins.orb;
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
                Path dir = FileSystems.getDefault().getPath(String.valueOf(getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + "/custom_origins/"));
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
                    for (Path path : stream) {
                        if (path.toFile().exists()) {
                                String maindir = path.toFile().getAbsolutePath();
                                File originpack_datamain = new File(String.valueOf(path), "data");
                                String datapath = originpack_datamain.getAbsolutePath();
                                File originpack_powermain = new File(String.valueOf(path), "powers");
                                String powerspath = originpack_powermain.getAbsolutePath();
                                File originpack_mainclass = new File(String.valueOf(datapath), "main.yml");
                            if(originpack_datamain.exists()){
                                if(originpack_powermain.exists()){
                                    if(originpack_mainclass.exists()){
                                        YamlConfiguration mainconfig = new YamlConfiguration();
                                        mainconfig = YamlConfiguration.loadConfiguration(originpack_mainclass);
                                        if (e.getView().getTitle().equalsIgnoreCase(LIGHT_PURPLE + "Origins Menu")) {
                                            if (p.hasPermission("genesismc.choosing.custom.canchoose")) {
                                                if (e.getCurrentItem() != null) {
                                                    if (e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
                                                        if (custom_folder.listFiles().length <= 21) {
                                                            Inventory customOriginsMenu = Bukkit.createInventory(p, 27, "Custom Origins");
                                                            ItemStack bar_wall = new ItemStack(Material.IRON_BARS);
                                                            ItemStack empty_slot = new ItemStack(AIR);

                                                            if (custom_folder.listFiles().length > 1) {

                                                            }
                                                            if (originpack_mainclass.exists()) {
                                                                if (mainconfig.getString("icon").equalsIgnoreCase(null)) {
                                                                    ItemStack custom_slot = new ItemStack(DIRT);
                                                                    custom_slot.setAmount(1);
                                                                    ItemMeta custommeta = custom_slot.getItemMeta();
                                                                    if (!mainconfig.getString("name").equalsIgnoreCase(null)) {
                                                                        custommeta.setDisplayName(mainconfig.getString("name"));
                                                                        custom_slot.setItemMeta(custommeta);
                                                                        ItemStack[] custom_items = {bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall, orb, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, orb, bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall};
                                                                        customOriginsMenu.setContents(custom_items);
                                                                        customOriginsMenu.addItem(custom_slot);
                                                                        p.openInventory(customOriginsMenu);
                                                                    } else {
                                                                        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] You need to provide a name for custom origin");
                                                                    }
                                                                } else {
                                                                    ItemStack custom_slot = new ItemStack(Material.valueOf(mainconfig.getString("icon")));
                                                                    custom_slot.setAmount(1);
                                                                    ItemMeta custommeta = custom_slot.getItemMeta();
                                                                    if (!mainconfig.getString("name").equalsIgnoreCase(null)) {
                                                                        custommeta.setDisplayName(mainconfig.getString("name"));
                                                                        custom_slot.setItemMeta(custommeta);
                                                                        customOriginsMenu.addItem(custom_slot);
                                                                        p.openInventory(customOriginsMenu);
                                                                        ItemStack[] custom_items = {bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall, orb, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, orb, bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall};
                                                                        customOriginsMenu.setContents(custom_items);
                                                                        customOriginsMenu.addItem(custom_slot);
                                                                        p.openInventory(customOriginsMenu);
                                                                    } else {
                                                                        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] You need to provide a name for custom origin");
                                                                    }

                                                                }

                                                            } else {
                                                                ItemStack[] custom_items = {bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall, orb, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, orb, bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall};
                                                                customOriginsMenu.setContents(custom_items);
                                                                p.openInventory(customOriginsMenu);
                                                            }
                                                        } else {
                                                            if (e.getView().getTitle().equalsIgnoreCase(LIGHT_PURPLE + "Origins Menu")) {
                                                                if (e.getCurrentItem() != null) {
                                                                    if (e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
                                                                        if (custom_folder.listFiles().length <= 21) {
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
                                            }
                                        }

                                        if(e.getView().getTitle().equalsIgnoreCase("Custom Origins")){
                                            if(e.isLeftClick() || e.isRightClick() || e.isShiftClick()){
                                                e.setCancelled(true);
                                            }
                                        }
                                    }else{
                                        if (e.getView().getTitle().equalsIgnoreCase(LIGHT_PURPLE + "Origins Menu")){
                                            if (e.getCurrentItem() != null) {
                                                if(e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
                                                    if (custom_folder.listFiles().length <= 21) {
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
                                }else{
                                    if (e.getView().getTitle().equalsIgnoreCase(LIGHT_PURPLE + "Origins Menu")){
                                        if (e.getCurrentItem() != null) {
                                            if(e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
                                                if (custom_folder.listFiles().length <= 21) {
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
                            }else{
                                if (e.getView().getTitle().equalsIgnoreCase(LIGHT_PURPLE + "Origins Menu")){
                                    if (e.getCurrentItem() != null) {
                                        if(e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
                                            if (custom_folder.listFiles().length <= 21) {
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
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }else{
            //do nothing
        }
    }
}
