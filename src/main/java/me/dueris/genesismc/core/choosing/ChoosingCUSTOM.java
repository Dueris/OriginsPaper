package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.custom_origins.CustomOriginsMethods;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemProperties;
import static me.dueris.genesismc.core.choosing.contents.MainMenuContents.GenesisMainMenuContents;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;

public class ChoosingCUSTOM implements Listener {

    public static HashMap<ItemStack, String> originIdentifier = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void CUSOTMCHOOSE_MENU(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
                @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Custom Origins");
                if (e.getCurrentItem().getType().equals(Material.TIPPED_ARROW)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);

                    ItemStack bars = new ItemStack(Material.IRON_BARS);
                    ItemStack back = new ItemStack(Material.ARROW);
                    ItemStack next = new ItemStack(Material.ARROW);
                    ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);

                    bars = itemProperties(bars, "", ItemFlag.HIDE_ENCHANTS, null, null);
                    menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
                    back = itemProperties(back, "Back", ItemFlag.HIDE_ENCHANTS, null, null);
                    next = itemProperties(next, "Next", ItemFlag.HIDE_ENCHANTS, null, null);

                    ArrayList<ItemStack> contents = new ArrayList<>();
                    ArrayList<String> customOriginTags = CustomOriginsMethods.getCustomOriginTags();

                    for (int i = 0; i <= 53; i++) {
                        if (i % 9 == 0 || (i + 1) % 9 == 0) {
                            contents.add(bars);
                        } else if (i == 46) {
                            contents.add(back);
                        } else if (i == 49) {
                            contents.add(menu);
                        } else if (i == 52) {
                            contents.add(next);
                        } else if (i >= 46) {
                            contents.add(new ItemStack(Material.AIR));
                        } else {
                            if (customOriginTags.size() > 0) {
                                String origintag = customOriginTags.get(0);
                                while (CustomOriginsMethods.getCustomOriginUnChoosable(origintag)) {
                                    customOriginTags.remove(0);
                                    origintag = customOriginTags.get(0);
                                }
                                String minecraftItem = CustomOriginsMethods.getCustomOriginIcon(origintag);
                                String item = minecraftItem.split(":")[1];
                                ItemStack originIcon = new ItemStack(Material.valueOf(item.toUpperCase()));

                                originIcon = itemProperties(originIcon, CustomOriginsMethods.getCustomOriginName(origintag), ItemFlag.HIDE_ENCHANTS, null, CustomOriginsMethods.getCustomOriginDescription(origintag));

                                originIdentifier.put(originIcon, origintag);
                                contents.add(originIcon);

                                customOriginTags.remove(0);
                            } else {
                                contents.add(new ItemStack(Material.AIR));
                            }
                        }
                    }

                    custommenu.setContents(contents.toArray(new ItemStack[0]));
                    e.getWhoClicked().openInventory(custommenu);

                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void CUSOTMCHOOSE_ORIGIN(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (originIdentifier.containsKey(e.getCurrentItem())) {
                @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Custom Origin");
                String origintag = originIdentifier.get(e.getCurrentItem());

                ArrayList<String> originPowerNames = new ArrayList<>();
                ArrayList<String> originPowerDescriptions = new ArrayList<>();

                for (String powerTag : CustomOriginsMethods.getCustomOriginPowers(origintag)) {
                    if (!CustomOriginsMethods.getCustomOriginPowerHidden(origintag, powerTag)) {
                        originPowerNames.add(CustomOriginsMethods.getCustomOriginPowerName(origintag, powerTag));
                        originPowerDescriptions.add(CustomOriginsMethods.getCustomOriginPowerDescription(origintag, powerTag));
                    }
                }

                ItemStack close = new ItemStack(Material.BARRIER);
                ItemStack back = new ItemStack(Material.SPECTRAL_ARROW);

                String minecraftItem = CustomOriginsMethods.getCustomOriginIcon(origintag);
                String item = minecraftItem.split(":")[1];
                ItemStack originIcon = new ItemStack(Material.valueOf(item.toUpperCase()));

                close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
                back = itemProperties(back, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);

                ItemMeta originIconmeta = originIcon.getItemMeta();
                originIconmeta.setDisplayName(CustomOriginsMethods.getCustomOriginName(origintag));
                originIconmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                ArrayList<String> originIconlore = new ArrayList<>();
                originIconlore.add(CustomOriginsMethods.getCustomOriginDescription(origintag));
                originIconmeta.setLore(originIconlore);
                originIcon.setItemMeta(originIconmeta);
                originIcon = itemProperties(originIcon, CustomOriginsMethods.getCustomOriginName(origintag), ItemFlag.HIDE_ENCHANTS, null, CustomOriginsMethods.getCustomOriginDescription(origintag));


                ArrayList<ItemStack> contents = new ArrayList<>();

                for (int i = 0; i <= 53; i++) {
                    if (i == 0 || i == 8) {
                        contents.add(close);
                    } else if (i == 4) {
                        contents.add(orb);
                    } else if (i == 13) {
                        contents.add(originIcon);
                    } else if ((i >=20 && i <= 24) || (i >=29 && i <= 33) || (i >=38 && i <= 42)) {

                        if (originPowerNames.size() > 0) {
                            String powerName = originPowerNames.get(0);
                            String powerDescription = originPowerDescriptions.get(0);

                            ItemStack originPower = new ItemStack(Material.FILLED_MAP);

                            ItemMeta meta = originPower.getItemMeta();
                            meta.setDisplayName(powerName);
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            ArrayList<String> lore = new ArrayList<>();
                            lore.add(powerDescription);
                            meta.setLore(lore);
                            originPower.setItemMeta(meta);

                            contents.add(originPower);
                            originPowerNames.remove(0);
                            originPowerDescriptions.remove(0);

                        } else {
                            if (i >=38) {
                                contents.add(new ItemStack(Material.AIR));
                            } else {
                                contents.add(new ItemStack(Material.PAPER));
                            }
                        }

                    } else if (i == 49) {
                        contents.add(back);
                    } else {
                        contents.add(new ItemStack(Material.AIR));
                    }
                }
                custommenu.setContents(contents.toArray(new ItemStack[0]));
                e.getWhoClicked().openInventory(custommenu);
            }
        }
    }

    @EventHandler
    public void onCustomOriginBack(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().equalsIgnoreCase("Custom Origin")) {
                if (e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)) {
                    @NotNull Inventory mainmenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Choosing Menu");
                    mainmenu.setContents(GenesisMainMenuContents()); //change it to choose menu later (when i'm not almost falling asleep)
                    e.getWhoClicked().openInventory(mainmenu);
                }else{e.setCancelled(true);}
            }
        }
    }

    @EventHandler
    public void onCustomOriginClose(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().equalsIgnoreCase("Custom Origin")) {
                if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
                    e.getWhoClicked().closeInventory();
                }else{e.setCancelled(true);}
            }
        }
    }
}

