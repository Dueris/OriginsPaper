package me.dueris.genesismc.core.factory.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CustomMenuHandler implements Listener {

    @EventHandler
    public static void onClickCustomOpen(InventoryClickEvent e) {
//        if(e.getWhoClicked() instanceof HumanEntity){
//            Player p = (Player) e.getWhoClicked();
//            File custom_folder = new File(getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");
//            if(custom_folder.listFiles().length >= 1) {
//                DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
//                    @Override
//                    public boolean accept(Path file) throws IOException {
//                        return (Files.isDirectory(file));
//                    }
//                };
//                if (e.getView().getTitle().equalsIgnoreCase(LIGHT_PURPLE + "Origins Menu")) {
//                      if (p.hasPermission("genesismc.choosing.custom.canchoose")) {
//                      if (e.getCurrentItem() != null) {
//                      if (e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
//                      if (e.getView().getTitle().equalsIgnoreCase(LIGHT_PURPLE + "Origins Menu")) {
//                                                                if (e.getCurrentItem() != null) {
//                                                                    if (e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
//                                                                            Inventory customOriginsMenu = Bukkit.createInventory(p, 27, "Custom Origins");
//                                                                            ItemStack bar_wall = new ItemStack(Material.IRON_BARS);
//                                                                            ItemStack empty_slot = new ItemStack(Material.RED_STAINED_GLASS_PANE);
//
//
//                                                                            ItemStack[] custom_items = {bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall, orb, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, orb, bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall};
//                                                                            customOriginsMenu.setContents(custom_items);
//                                                                            p.openInventory(customOriginsMenu);
//                                                                    }
//                                                                }
//                                                            }
//                                                    }
//                                                }
//                                            }
//                                        }
//
//                                        if(e.getView().getTitle().equalsIgnoreCase("Custom Origins")){
//                                            if(e.isLeftClick() || e.isRightClick() || e.isShiftClick()){
//                                                e.setCancelled(true);
//                                            }
//                                        }
//                        }else{
//                            if (e.getView().getTitle().equalsIgnoreCase(LIGHT_PURPLE + "Origins Menu")) {
//                                if (e.getCurrentItem() != null) {
//                                    if (e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
//                                        if (custom_folder.listFiles().length <= 21) {
//                                            Inventory customOriginsMenu = Bukkit.createInventory(p, 27, "Custom Origins");
//                                            ItemStack bar_wall = new ItemStack(Material.IRON_BARS);
//                                            ItemStack empty_slot = new ItemStack(Material.RED_STAINED_GLASS_PANE);
//
//
//                                            ItemStack[] custom_items = {bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall, orb, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, orb, bar_wall, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, empty_slot, bar_wall};
//                                            customOriginsMenu.setContents(custom_items);
//                                            p.openInventory(customOriginsMenu);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
            }
    }
