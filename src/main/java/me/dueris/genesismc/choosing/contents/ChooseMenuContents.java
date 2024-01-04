package me.dueris.genesismc.choosing.contents;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static me.dueris.genesismc.choosing.ChoosingMain.itemProperties;
import static me.dueris.genesismc.choosing.ChoosingCustomOrigins.cutStringIntoLists;

public class ChooseMenuContents {

    public static @Nullable ItemStack @NotNull [] ChooseMenuContent(int pageNumber, LayerContainer choosingLayer) {
        ItemStack sides = itemProperties(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "", ItemFlag.HIDE_ENCHANTS, null, null);
        ItemStack menu = itemProperties(new ItemStack(Material.SPECTRAL_ARROW), ChatColor.AQUA + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "menu.customChoose.return"), ItemFlag.HIDE_ENCHANTS, null, null);
        ItemStack back = itemProperties(new ItemStack(Material.ARROW), LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "menu.customChoose.back"), ItemFlag.HIDE_ENCHANTS, null, null);
        ItemStack next = itemProperties(new ItemStack(Material.ARROW), LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "menu.customChoose.next"), ItemFlag.HIDE_ENCHANTS, null, null);

        ArrayList<ItemStack> contents = new ArrayList<>();
        ArrayList<OriginContainer> originContainers = new ArrayList<>(CraftApoli.getOrigins());

        //removes the core origins
        originContainers.removeIf(CraftApoli::isCoreOrigin);

        //removes origins not in the layer
        for (int i = 0; i < originContainers.size(); i++) {
            OriginContainer origin = originContainers.get(i);
            if (!choosingLayer.getOrigins().contains(origin.getTag())) originContainers.remove(origin);
        }

        //removes the origins that can't be displayed on the page due to page size constraints.
        for (int i = 0; 35 * pageNumber > i; i++) {
            if (originContainers.isEmpty()) break;
            originContainers.remove(0);
        }

        //sets which page the arrows will go to
        NamespacedKey pageKey = new NamespacedKey(GenesisMC.getPlugin(), "page");
        ItemMeta backMeta = back.getItemMeta();
        if (pageNumber == 0) backMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, 0);
        else backMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, pageNumber - 1);
        back.setItemMeta(backMeta);

        ItemMeta nextMeta = next.getItemMeta();
        if (originContainers.size() < 37)
            nextMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, pageNumber);
        else nextMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, pageNumber + 1);
        next.setItemMeta(nextMeta);


        //generates the menu
        for (int i = 0; i <= 53; i++) {
            if (i % 9 == 0 || (i + 1) % 9 == 0) {
                contents.add(sides);
            } else if (i == 46) {
                contents.add(back);
            } else if (i == 49) {
                contents.add(menu);
            } else if (i == 52) {
                contents.add(next);
            } else if (i >= 46) {
                contents.add(new ItemStack(Material.AIR));
            } else {
                if (originContainers.size() > 0) {
                    OriginContainer origin = originContainers.get(0);
                    while (origin.getUnchooseable()) {
                        originContainers.remove(0);
                        origin = originContainers.get(0);
                    }
                    String minecraftItem = origin.getIcon();
                    String item = null;
                    if(minecraftItem.contains(":")){
                        item = minecraftItem.split(":")[1];
                    }else{
                        item = minecraftItem;
                    }
                    ItemStack originIcon = new ItemStack(Material.valueOf(item.toUpperCase()));

                    ItemMeta originIconmeta = originIcon.getItemMeta();
                    originIconmeta.setDisplayName(origin.getName());
                    originIconmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    originIconmeta.setLore(cutStringIntoLists(origin.getDescription()));
                    NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originTag");
                    originIconmeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, origin.getTag());
                    originIcon.setItemMeta(originIconmeta);
                    contents.add(originIcon);
                    originContainers.remove(0);
                } else {
                    contents.add(new ItemStack(Material.AIR));
                }
            }
        }

        return contents.toArray(new ItemStack[0]);

    }
}
