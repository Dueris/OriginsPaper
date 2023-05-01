package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.items.OrbOfOrigins;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ChoosingCUSTOM implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void CUSOTMCHOOSE_MENU(InventoryClickEvent e){
        if(e.getCurrentItem() != null){
            if(e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
                PersistentDataContainer data = e.getWhoClicked().getPersistentDataContainer();
                @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
                @NotNull Inventory custommenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Custom Origins");
                if (e.getCurrentItem().getType().equals(Material.TIPPED_ARROW)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);

                    ItemStack empty = new ItemStack(Material.AIR);
                    ItemStack bars = new ItemStack(Material.IRON_BARS);
                    ItemStack back = new ItemStack(Material.ARROW);
                    ItemStack next = new ItemStack(Material.ARROW);
                    ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
                    ItemStack empno = new ItemStack(Material.AIR);

                    ItemMeta barmeta = bars.getItemMeta();
                    barmeta.setDisplayName("");
                    barmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    ArrayList<String> barlore = new ArrayList<>();
                    barmeta.setLore(barlore);
                    bars.setItemMeta(barmeta);

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

                    ItemStack[] contents = {bars, empty, empty, empty, empty, empty, empty, empty, bars,
                            bars, empty, empty, empty, empty, empty, empty, empty, bars,
                            bars, empty, empty, empty, empty, empty, empty, empty, bars,
                            bars, empty, empty, empty, empty, empty, empty, empty, bars,
                            bars, empty, empty, empty, empty, empty, empty, empty, bars, bars, back, empno, empno, menu, empno, empno, next, bars //
                    };
                    custommenu.setContents(contents);
                    e.getWhoClicked().openInventory(custommenu);

                }
            }
        }
    }

}
