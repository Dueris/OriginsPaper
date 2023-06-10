package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoliRewriten;
import me.dueris.genesismc.core.factory.powers.world.WorldSpawnHandler;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
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
import java.util.List;
import java.util.Objects;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.*;
import static me.dueris.genesismc.core.choosing.contents.ChooseMenuContents.ChooseMenuContent;
import static me.dueris.genesismc.core.choosing.contents.MainMenuContents.GenesisMainMenuContents;
import static me.dueris.genesismc.core.factory.powers.Powers.*;
import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;

public class ChoosingCUSTOM implements Listener {

    public static List<String> cutStringIntoLists(String string) {
        ArrayList<String> strings = new ArrayList<>();
        int startStringLength = string.length();
        while (string.length() > 40) {
            for (int i = 40; i > 1; i--) {
                if (String.valueOf(string.charAt(i)).equals(" ")) {
                    strings.add(string.substring(0, i));
                    string = string.substring(i + 1);
                    break;
                }
            }
            if (startStringLength == string.length()) return List.of(string);
        }
        if (strings.isEmpty()) return List.of(string);
        strings.add(string);
        return strings.stream().toList();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void CUSOTMCHOOSE_MENU(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
                @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Custom Origins");
                if (e.getCurrentItem().getType().equals(Material.TIPPED_ARROW)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
                    custommenu.setContents(ChooseMenuContent());
                    e.getWhoClicked().openInventory(custommenu);

                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OriginChooseMenu(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().getItemMeta() == null) return;
            NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originTag");
            if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) != null) {
                @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Origin");
                String originTag = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (originTag == null) return;

                OriginContainer origin = null;
                for (OriginContainer origins : CraftApoliRewriten.getOrigins()) {
                    if (origins.getTag().equals(originTag)) {
                        origin = origins;
                        break;
                    }
                }

                Player p = (Player) e.getWhoClicked();
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);

                ArrayList<PowerContainer> powerContainers = origin.getPowerContainers();

                String minecraftItem = origin.getIcon();
                String item = minecraftItem.split(":")[1];
                ItemStack originIcon = new ItemStack(Material.valueOf(item.toUpperCase()));

                ItemStack close = itemProperties(new ItemStack(Material.BARRIER), RED + "Close", null, null, RED + "Cancel Choosing");
                ItemStack back = itemProperties(new ItemStack(Material.SPECTRAL_ARROW), ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
                ItemStack lowImpact = itemProperties(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.GREEN + "Low", null, null, null);
                ItemStack mediumImpact = itemProperties(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.YELLOW + "Medium", null, null, null);
                ItemStack highImpact = itemProperties(new ItemStack(Material.RED_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.RED + "High", null, null, null);


                ItemMeta originIconmeta = originIcon.getItemMeta();
                originIconmeta.setDisplayName(origin.getName());
                originIconmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                originIconmeta.setLore(cutStringIntoLists(origin.getDescription()));
                originIconmeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, originTag);
                NamespacedKey chooseKey = new NamespacedKey(GenesisMC.getPlugin(), "originChoose");
                originIconmeta.getPersistentDataContainer().set(chooseKey, PersistentDataType.INTEGER, 1);

                originIcon.setItemMeta(originIconmeta);

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
                        contents.add(orb);
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
                        while (powerContainers.size() > 0 && powerContainers.get(0).getHidden()) {
                            powerContainers.remove(0);
                        }
                        if (powerContainers.size() > 0) {

                            ItemStack originPower = new ItemStack(Material.FILLED_MAP);

                            ItemMeta meta = originPower.getItemMeta();
                            meta.setDisplayName(powerContainers.get(0).getName());
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            meta.setLore(cutStringIntoLists(powerContainers.get(0).getDesription()));
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
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OriginSelect(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().getType() == Material.MAGMA_CREAM) return;
            if (e.getCurrentItem().getItemMeta() == null) return;
            NamespacedKey chooseKey = new NamespacedKey(GenesisMC.getPlugin(), "originChoose");
            if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(chooseKey, PersistentDataType.INTEGER) != null) {
                NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originTag");
                String originTag = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                OriginContainer origin = CraftApoliRewriten.getOrigin(originTag);
                Player p = (Player) e.getWhoClicked();
                if (origin == null) {
                    p.sendMessage(Component.text("This origin is null ¯\\_(ツ)_/¯"));
                    return;
                }
                setAttributesToDefault(p);
                Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                    OriginPlayer.unassignPowers(p);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY, CraftApoliRewriten.toByteArray(origin));
                    OriginPlayer.assignPowers(p);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    DefaultChoose.DefaultChoose(p);
                    removeItemPhantom(p);
                    removeItemEnder(p);
                    removeItemElytrian(p);
                    p.getScoreboardTags().remove("choosing");
                }, 1);
                Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                    if (launch_into_air.contains(p)) {
                        ItemStack launchitem = new ItemStack(Material.FEATHER);
                        ItemMeta launchmeta = launchitem.getItemMeta();
                        launchmeta.setDisplayName(GRAY + "Launch");
                        launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        launchitem.setItemMeta(launchmeta);
                        p.getInventory().addItem(launchitem);
                    }
                    if (throw_ender_pearl.contains(p)) {
                        ItemStack infinpearl = new ItemStack(Material.ENDER_PEARL);
                        ItemMeta pearl_meta = infinpearl.getItemMeta();
                        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
                        ArrayList<String> pearl_lore = new ArrayList();
                        pearl_meta.setUnbreakable(true);
                        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        pearl_meta.setLore(pearl_lore);
                        infinpearl.setItemMeta(pearl_meta);
                        p.getInventory().addItem(infinpearl);
                    }
                    if (phantomize.contains(p)) {
                        ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
                        ItemMeta switch_meta = spectatorswitch.getItemMeta();
                        switch_meta.setDisplayName(GRAY + "Phantom Form");
                        ArrayList<String> pearl_lore = new ArrayList();
                        switch_meta.setUnbreakable(true);
                        switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                        switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        switch_meta.setLore(pearl_lore);
                        spectatorswitch.setItemMeta(switch_meta);
                        p.getInventory().addItem(spectatorswitch);
                    }
                    if (nether_spawn.contains(p) && p.getBedSpawnLocation() == null)
                        p.teleport(WorldSpawnHandler.NetherSpawn());
                }, 2);
            }
        }
    }

    @EventHandler
    public void OriginBack(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().equalsIgnoreCase("Origin")) {
                if (e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
                    NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originTag");

                    for (OriginContainer origin : CraftApoliRewriten.getCoreOrigins()) {
                        if (Objects.equals(e.getClickedInventory().getContents()[13].getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), origin.getTag())) {
                            @NotNull Inventory mainmenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Choosing Menu");
                            mainmenu.setContents(GenesisMainMenuContents((Player) e.getWhoClicked()));
                            e.getWhoClicked().openInventory(mainmenu);
                            return;
                        }
                    }

                    @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Custom Origins");
                    custommenu.setContents(ChooseMenuContent());
                    e.getWhoClicked().openInventory(custommenu);
                } else e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void OriginClose(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().equalsIgnoreCase("Origin")) {
                if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
                    e.getWhoClicked().closeInventory();
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }
}

