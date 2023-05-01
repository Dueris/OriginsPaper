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


public class ChoosingCUSTOM implements Listener {

    static ArrayList<ItemStack> contents = new ArrayList<>();
    static ArrayList<String> customOriginTags = CustomOriginsMethods.getCustomOriginTags();

    @EventHandler(priority = EventPriority.HIGH)
    public void CUSOTMCHOOSE_MENU(InventoryClickEvent e){
        if(e.getCurrentItem() != null){
            if(e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
                @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Custom Origins");
                if (e.getCurrentItem().getType().equals(Material.TIPPED_ARROW)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);

                    ItemStack back = new ItemStack(Material.ARROW);
                    ItemStack next = new ItemStack(Material.ARROW);
                    ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);

//                    ItemMeta barmeta = bars.getItemMeta();
//                    barmeta.setDisplayName("");
//                    barmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//                    ArrayList<String> barlore = new ArrayList<>();
//                    barmeta.setLore(barlore);
//                    bars.setItemMeta(barmeta);

                    ItemMeta menumeta = menu.getItemMeta();
                    menumeta.setDisplayName(ChatColor.AQUA + "Return");
                    menumeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    ArrayList<String> menulore = new ArrayList<>();
                    menumeta.setLore(menulore);
                    menu.setItemMeta(menumeta);

                    ItemMeta backmeta = back.getItemMeta();
                    backmeta.setDisplayName("Back");
                    backmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    ArrayList<String> backlore = new ArrayList<>();
                    backmeta.setLore(backlore);
                    back.setItemMeta(backmeta);

                    ItemMeta nextmeta = next.getItemMeta();
                    nextmeta.setDisplayName("Next");
                    nextmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    ArrayList<String> nextlore = new ArrayList<>();
                    nextmeta.setLore(nextlore);
                    next.setItemMeta(nextmeta);


                    for (int i = 0; i <= 53; i++) {
                        if (i%9 == 0 || (i+1)%9 == 0) {
                            contents.add(new ItemStack(Material.IRON_BARS));
                        } else if (i==46) {
                            contents.add(back);
                        } else if (i==49) {
                            contents.add(menu);
                        } else if (i==52) {
                            contents.add(next);
                        } else if (i >= 46) {
                            contents.add(new ItemStack(Material.AIR));
                        } else {
                            displayNextOrigin();
                        }
                    }

                    custommenu.setContents(contents.toArray(new ItemStack[0]));
                    e.getWhoClicked().openInventory(custommenu);

                }
            }
        }
    }

    public static void displayNextOrigin() {
        if (customOriginTags.size() > 0) {
            String origintag = customOriginTags.get(0);
            System.out.println(CustomOriginsMethods.getCustomOriginUnChoosable(origintag));
            if (CustomOriginsMethods.getCustomOriginUnChoosable(origintag)) {
                customOriginTags.remove(0);
                try {
                    displayNextOrigin();
                } catch (Exception e) {
                    ;
                }
            } else {
                String minecraftItem = CustomOriginsMethods.getCustomOriginIcon(origintag);
                String item = minecraftItem.split(":")[1];
                ItemStack originIcon = new ItemStack(Material.valueOf(item.toUpperCase()));

                ItemMeta meta = originIcon.getItemMeta();
                meta.setDisplayName(CustomOriginsMethods.getCustomOriginName(origintag));
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CustomOriginsMethods.getCustomOriginDescription(origintag));
                meta.setLore(lore);
                originIcon.setItemMeta(meta);

                contents.add(originIcon);

                customOriginTags.remove(0);
            }
        } else {
            contents.add(new ItemStack(Material.AIR));
        }
    }
}
