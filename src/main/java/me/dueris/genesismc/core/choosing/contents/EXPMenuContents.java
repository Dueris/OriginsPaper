package me.dueris.genesismc.core.choosing.contents;

import me.dueris.genesismc.core.items.OrbOfOrigins;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class EXPMenuContents {

    public static ItemStack[] EXPContents(){
        ItemStack empty = new ItemStack(Material.AIR);
        ItemStack orb = OrbOfOrigins.orb;
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
                bars, empty, empty, empty, empty, empty, empty, empty, bars, bars, empno, empno, empno, menu, empno, empno, empno, bars //
        };

        return contents;
    }

}
