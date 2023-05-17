package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.api.factory.CustomOriginAPI;
import me.dueris.genesismc.core.factory.powers.world.WorldSpawnHandler;
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
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.*;
import static me.dueris.genesismc.core.choosing.contents.ChooseMenuContents.ChooseMenuContent;
import static org.bukkit.ChatColor.RED;
import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;

public class ChoosingCUSTOM implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void CUSOTMCHOOSE_MENU(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
                @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Custom Origins");
                if (e.getCurrentItem().getType().equals(Material.TIPPED_ARROW)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                    custommenu.setContents(ChooseMenuContent());
                    e.getWhoClicked().openInventory(custommenu);

                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void CUSOTMCHOOSE_ORIGIN(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().getItemMeta() == null) return;
            NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originTag");
            if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) != null) {
                @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Custom Origin");
                String origintag = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (origintag == null) return;

                Player p = (Player) e.getWhoClicked();
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);


                ArrayList<String> originPowerNames = new ArrayList<>();
                ArrayList<String> originPowerDescriptions = new ArrayList<>();

                for (String powerTag : CustomOriginAPI.getCustomOriginPowers(origintag)) {
                    if (!CustomOriginAPI.getCustomOriginPowerHidden(origintag, powerTag)) {
                        originPowerNames.add(CustomOriginAPI.getCustomOriginPowerName(origintag, powerTag));
                        originPowerDescriptions.add(CustomOriginAPI.getCustomOriginPowerDescription(origintag, powerTag));
                    }
                }

                String minecraftItem = CustomOriginAPI.getCustomOriginIcon(origintag);
                String item = minecraftItem.split(":")[1];
                ItemStack originIcon = new ItemStack(Material.valueOf(item.toUpperCase()));

                ItemStack close = itemProperties(new ItemStack(Material.BARRIER), RED + "Close", null, null, RED + "Cancel Choosing");
                ItemStack back = itemProperties(new ItemStack(Material.SPECTRAL_ARROW), ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
                ItemStack lowImpact = itemProperties(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.GREEN + "Low", null, null, null);
                ItemStack mediumImpact = itemProperties(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.YELLOW + "Medium", null, null, null);
                ItemStack highImpact = itemProperties(new ItemStack(Material.RED_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.RED + "High", null, null, null);


                ItemMeta originIconmeta = originIcon.getItemMeta();
                originIconmeta.setDisplayName(CustomOriginAPI.getCustomOriginName(origintag));
                originIconmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                originIconmeta.setLore(cutStringIntoLists(CustomOriginAPI.getCustomOriginDescription(origintag)));
                originIconmeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, origintag);
                NamespacedKey chooseKey = new NamespacedKey(GenesisMC.getPlugin(), "originChoose");
                originIconmeta.getPersistentDataContainer().set(chooseKey, PersistentDataType.INTEGER, 1);

                originIcon.setItemMeta(originIconmeta);

                ArrayList<ItemStack> contents = new ArrayList<>();
                long impact = CustomOriginAPI.getCustomOriginImpact(origintag);

                for (int i = 0; i <= 53; i++) {
                    if (i == 0 || i == 8) {
                        contents.add(close);
                    }
                    else if (i == 1) {                                          //impact
                        if (impact == 1) contents.add(lowImpact);
                        if (impact == 2) contents.add(mediumImpact);
                        if (impact == 3) contents.add(highImpact);
                    } else if (i == 2) {
                        if (impact == 2) contents.add(mediumImpact);
                        else if (impact == 3) contents.add(highImpact);
                        else contents.add(new ItemStack(Material.AIR));
                    } else if (i == 3){
                        if (impact == 3) contents.add(highImpact);
                        else contents.add(new ItemStack(Material.AIR));
                    }
                    else if (i == 4) {
                        contents.add(orb);
                    }
                    else if (i == 5){                                           //impact
                        if (impact == 3) contents.add(highImpact);
                        else contents.add(new ItemStack(Material.AIR));
                    } else if (i == 6){
                        if (impact == 2) contents.add(mediumImpact);
                        else if (impact == 3) contents.add(highImpact);
                        else contents.add(new ItemStack(Material.AIR));
                    }  else if (i == 7) {
                        if (impact == 1) contents.add(lowImpact);
                        if (impact == 2) contents.add(mediumImpact);
                        if (impact == 3) contents.add(highImpact);
                    }
                    else if (i == 13) {
                        contents.add(originIcon);
                    } else if ((i >=20 && i <= 24) || (i >=29 && i <= 33) || (i >=38 && i <= 42)) {

                        if (originPowerNames.size() > 0) {
                            String powerName = originPowerNames.get(0);
                            String powerDescription = originPowerDescriptions.get(0);

                            ItemStack originPower = new ItemStack(Material.FILLED_MAP);

                            ItemMeta meta = originPower.getItemMeta();
                            meta.setDisplayName(powerName);
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            meta.setLore(cutStringIntoLists(powerDescription));
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

    @EventHandler(priority = EventPriority.HIGH)
    public void CustomOriginSelect(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().getType() == Material.MAGMA_CREAM) return;
            if (e.getCurrentItem().getItemMeta() == null) return;
            NamespacedKey chooseKey = new NamespacedKey(GenesisMC.getPlugin(), "originChoose");
            if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(chooseKey, PersistentDataType.INTEGER) != null) {
                NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originTag");
                String origintag = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                Player p = (Player) e.getWhoClicked();
                setAttributesToDefault(p);
                Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                    if (CustomOriginAPI.getCustomOriginPowers(origintag).contains("origins:nether_spawn") && p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING).equals("genesis:origin-null")) p.teleport(WorldSpawnHandler.NetherSpawn());
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, origintag);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    DefaultChoose.DefaultChoose(p);
                    removeItemPhantom(p);
                    removeItemEnder(p);
                },1);
            }
        }
    }

    @EventHandler
    public void onCustomOriginBack(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().equalsIgnoreCase("Custom Origin")) {
                if (e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                    @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Custom Origins");
                    custommenu.setContents(ChooseMenuContent());
                    e.getWhoClicked().openInventory(custommenu);
                } else e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCustomOriginClose(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().equalsIgnoreCase("Custom Origin")) {
                if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                    e.getWhoClicked().closeInventory();
                }else{e.setCancelled(true);}
            }
        }
    }

    public static List<String> cutStringIntoLists(String string) {
        ArrayList<String> strings = new ArrayList<>();
        while (string.length() > 40) {
            for (int i = 40; i > 1; i--) {
                if (String.valueOf(string.charAt(i)).equals(" ")) {
                    strings.add(string.substring(0, i));
                    string = string.substring(i+1);
                    break;
                }
            }
        }
        if (strings.isEmpty()) return Arrays.asList(string);
        strings.add(string);
        return strings.stream().toList();
    }
}

