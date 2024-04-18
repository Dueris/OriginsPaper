package me.dueris.genesismc.screen;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.content.OrbOfOrigins;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.ComponentMultiLine;
import me.dueris.genesismc.util.KeybindingUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static me.dueris.genesismc.content.OrbOfOrigins.orb;
import static me.dueris.genesismc.screen.OriginChoosing.choosing;
import static me.dueris.genesismc.screen.ScreenConstants.cutStringIntoLines;
import static me.dueris.genesismc.screen.ScreenConstants.itemProperties;
import static me.dueris.genesismc.screen.contents.ScreenContent.ChooseMenuContent;
import static me.dueris.genesismc.screen.contents.ScreenContent.GenesisMainMenuContents;

public class ScreenNavigator implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void CustomOriginMenu(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getView().getTitle().startsWith("Choosing Menu")) {
            Player p = (Player) e.getWhoClicked();
            Layer displayingLayer = choosing.get(p);
            String title = "Custom Origins - " + (choosing.get(p).isPresent("name") ? choosing.get(p).getName() : choosing.get(p).getTag());
            if (displayingLayer.isPresent("gui_title") && displayingLayer.getJsonObject("gui_title").isPresent("choose_origin")) {
                title = displayingLayer.getJsonObject("gui_titile").getString("choose_origin");
            }
            @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, title);
            if (e.getCurrentItem().getType().equals(Material.TIPPED_ARROW)) {
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
                custommenu.setContents(ChooseMenuContent(0, choosing.get(p), p));
                e.getWhoClicked().openInventory(custommenu);

            }
        }
    }

    @EventHandler
    public void OnInteractCancel(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().startsWith("Custom Origins")) {
                if (e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
                    Layer displayingLayer = choosing.get(p);
                    String title = "Choosing Menu - " + (choosing.get(p).isPresent("name") ? choosing.get(p).getName() : choosing.get(p).getTag());
                    if (displayingLayer.isPresent("gui_title") && displayingLayer.getJsonObject("gui_title").isPresent("choose_origin")) {
                        title = displayingLayer.getJsonObject("gui_titile").getString("choose_origin");
                    }
                    @NotNull Inventory mainmenu = Bukkit.createInventory(e.getWhoClicked(), 54, title);
                    mainmenu.setContents(GenesisMainMenuContents((Player) e.getWhoClicked()));
                    e.getWhoClicked().openInventory(mainmenu);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OriginChooseMenu(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getItemMeta() == null) return;
        if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Origin not found")) return;

        NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originTag");
        if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) != null && !e.getView().getTitle().startsWith("Origin")) {
            Player p = (Player) e.getWhoClicked();
            String originTag = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (originTag == null) return;

            //gets the origin container from the tag
            Origin origin = null;
            for (Origin origins : CraftApoli.getOriginsFromRegistry()) {
                if (origins.getTag().equals(originTag)) {
                    origin = origins;
                    break;
                }
            }

            Layer displayingLayer = choosing.get(p);
            String title = "Origins - " + origin.getName();
            if (displayingLayer.isPresent("gui_title") && displayingLayer.getJsonObject("gui_title").isPresent("view_origin")) {
                title = displayingLayer.getJsonObject("gui_titile").getString("view_origin");
            }
            @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, title);

            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

            ArrayList<Power> powerContainers = new ArrayList<>();
            for (Power powerContainer : origin.getPowerContainers()) {
                if (powerContainer.isHidden()) continue;
                powerContainers.add(powerContainer);
            }

            //gets icon from origin
            String minecraftItem = origin.getIcon();
            String item = null;
            if (minecraftItem.contains(":")) {
                item = minecraftItem.split(":")[1];
            } else {
                item = minecraftItem;
            }
            ItemStack originIcon = new ItemStack(Material.valueOf(item.toUpperCase()));

            //making the items to display in the menu
            ItemStack back = itemProperties(new ItemStack(Material.SPECTRAL_ARROW), ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
            ItemStack lowImpact = itemProperties(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.GREEN + "Low", null, null, null);
            ItemStack mediumImpact = itemProperties(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.YELLOW + "Medium", null, null, null);
            ItemStack highImpact = itemProperties(new ItemStack(Material.RED_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.RED + "High", null, null, null);
            ItemStack noImpact = itemProperties(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.GRAY + "None", null, null, null);

            //adds a key to the item that will be used later to get the origin from it
            ItemMeta originIconmeta = originIcon.getItemMeta();
            originIconmeta.displayName(ComponentMultiLine.apply(origin.getName()));
            originIconmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            originIconmeta.lore(ComponentMultiLine.apply(cutStringIntoLines(origin.getDescription())));
            originIconmeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, originTag);
            NamespacedKey chooseKey = new NamespacedKey(GenesisMC.getPlugin(), "originChoose");
            originIconmeta.getPersistentDataContainer().set(chooseKey, PersistentDataType.INTEGER, 1);
            originIcon.setItemMeta(originIconmeta);

            ArrayList<ItemStack> contents = new ArrayList<>();
            long impact = origin.getImpact();

            //generates menu
            for (int i = 0; i <= 53; i++) {
                if (i == 0 || i == 8) {
                    if (impact == 1) contents.add(lowImpact);
                    else if (impact == 2) contents.add(mediumImpact);
                    else if (impact == 3) contents.add(highImpact);
                    else if (impact == 0) contents.add(noImpact);
                    else contents.add(new ItemStack(Material.AIR));
                } else if (i == 1 || i == 7) {
                    if (impact == 2) contents.add(mediumImpact);
                    else if (impact == 3) contents.add(highImpact);
                    else if (impact == 0) contents.add(noImpact);
                    else contents.add(new ItemStack(Material.AIR));
                } else if (i == 2 || i == 6) {
                    if (impact == 3) contents.add(highImpact);
                    else if (impact == 0) contents.add(noImpact);
                    else contents.add(new ItemStack(Material.AIR));
                } else if (i == 3) {
                    contents.add(new ItemStack(Material.AIR));
                } else if (i == 4) {
                    contents.add(OrbOfOrigins.orb);
                } else if (i == 5) {
                    contents.add(new ItemStack(Material.AIR));
                } else if (i == 13) {
                    if (originIcon.getType().equals(Material.PLAYER_HEAD)) {
                        SkullMeta skull_p = (SkullMeta) originIcon.getItemMeta();
                        skull_p.setOwningPlayer(p);
                        skull_p.setOwner(p.getName());
                        skull_p.setPlayerProfile(p.getPlayerProfile());
                        skull_p.setOwnerProfile(p.getPlayerProfile());
                        originIcon.setItemMeta(skull_p);
                    }
                    contents.add(originIcon);
                } else if ((i >= 20 && i <= 24) || (i >= 29 && i <= 33) || (i >= 38 && i <= 42)) {
                    while (powerContainers.size() > 0 && (powerContainers.get(0).isHidden()
                        || (powerContainers.get(0).getName().equalsIgnoreCase("No Name")
                        && powerContainers.get(0).getDescription().equalsIgnoreCase("No Description")))) {
                        powerContainers.remove(0);
                    }
                    if (powerContainers.size() > 0) {
                        ItemStack originPower = new ItemStack(Material.FILLED_MAP);

                        ItemMeta meta = originPower.getItemMeta();
                        meta.displayName(ComponentMultiLine.apply(powerContainers.get(0).getName()));
                        if (KeybindingUtils.renderKeybind(powerContainers.get(0)).getFirst()) {
                            meta.displayName(Component.text().append(meta.displayName()).append(Component.text(" ")).append(Component.text(KeybindingUtils.translateOriginRawKey(KeybindingUtils.renderKeybind(powerContainers.get(0)).getSecond())).color(TextColor.color(32222))).build());
                        }
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        meta.lore(ComponentMultiLine.apply(cutStringIntoLines(powerContainers.get(0).getDescription())));
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

    @EventHandler
    public void OriginBack(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().startsWith("Origin")) {
                if (e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
                    NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originTag");

                    for (Origin origin : CraftApoli.getOriginsFromRegistry().stream().filter(origin -> CraftApoli.isCoreOrigin(origin)).toList()) {
                        if (Objects.equals(e.getClickedInventory().getContents()[13].getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), origin.getTag())) {
                            Layer displayingLayer = choosing.get(p);
                            String title = "Choosing Menu - " + (choosing.get(p).isPresent("name") ? choosing.get(p).getName() : choosing.get(p).getTag());
                            if (displayingLayer.isPresent("gui_title") && displayingLayer.getJsonObject("gui_title").isPresent("choose_origin")) {
                                title = displayingLayer.getJsonObject("gui_titile").getString("choose_origin");
                            }
                            @NotNull Inventory mainmenu = Bukkit.createInventory(e.getWhoClicked(), 54, title);
                            mainmenu.setContents(GenesisMainMenuContents((Player) e.getWhoClicked()));
                            e.getWhoClicked().openInventory(mainmenu);
                            return;
                        }
                    }

                    Layer displayingLayer = choosing.get(p);
                    String title = "Custom Origins - " + (choosing.get(p).isPresent("name") ? choosing.get(p).getName() : choosing.get(p).getTag());
                    if (displayingLayer.isPresent("gui_title") && displayingLayer.getJsonObject("gui_title").isPresent("choose_origin")) {
                        title = displayingLayer.getJsonObject("gui_titile").getString("choose_origin");
                    }
                    @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, title);
                    custommenu.setContents(ChooseMenuContent(0, choosing.get(p), p));
                    e.getWhoClicked().openInventory(custommenu);
                }
            }
        }
    }

    @EventHandler
    public void scroll(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().startsWith("Custom Origins")) {
                ItemStack item = e.getCurrentItem();
                if (item.getType().equals(Material.ARROW) && (e.getCurrentItem().getItemMeta().getDisplayName().equals("Previous Page") || e.getCurrentItem().getItemMeta().getDisplayName().equals("Next Page"))) {
                    Player p = (Player) e.getWhoClicked();
                    Layer displayingLayer = choosing.get(p);
                    String title = "Custom Origins - " + (choosing.get(p).isPresent("name") ? choosing.get(p).getName() : choosing.get(p).getTag());
                    if (displayingLayer.isPresent("gui_title") && displayingLayer.getJsonObject("gui_title").isPresent("choose_origin")) {
                        title = displayingLayer.getJsonObject("gui_titile").getString("choose_origin");
                    }
                    @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, title);
                    NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "page");
                    custommenu.setContents(ChooseMenuContent(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER), choosing.get(p), p));
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().openInventory(custommenu);
                }
            }
        }
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (choosing.containsKey(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }
}


