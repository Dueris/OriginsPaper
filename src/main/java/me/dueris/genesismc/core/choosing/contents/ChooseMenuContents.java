package me.dueris.genesismc.core.choosing.contents;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.OriginContainer;
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

import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemProperties;
import static me.dueris.genesismc.core.choosing.ChoosingCUSTOM.cutStringIntoLists;

public class ChooseMenuContents {

    public static @Nullable ItemStack @NotNull [] ChooseMenuContent(int pageNumber) {
        ItemStack sides = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemStack back = new ItemStack(Material.ARROW);
        ItemStack next = new ItemStack(Material.ARROW);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);

        sides = itemProperties(sides, "", ItemFlag.HIDE_ENCHANTS, null, null);
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        back = itemProperties(back, "Back", ItemFlag.HIDE_ENCHANTS, null, null);
        next = itemProperties(next, "Next", ItemFlag.HIDE_ENCHANTS, null, null);

        ArrayList<ItemStack> contents = new ArrayList<>();
        ArrayList<OriginContainer> originContainers = new ArrayList<>(CraftApoli.getOrigins());

        originContainers.removeIf(CraftApoli::isCoreOrigin);
        for (int i = 0; 35 * pageNumber > i; i++) {
            if (originContainers.isEmpty()) break;
            originContainers.remove(0);
        }

        NamespacedKey pageKey = new NamespacedKey(GenesisMC.getPlugin(), "page");
        ItemMeta backMeta = back.getItemMeta();
        if (pageNumber == 0) backMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, 0);
        else backMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, pageNumber-1);
        back.setItemMeta(backMeta);


        ItemMeta nextMeta = next.getItemMeta();
        if (originContainers.size() < 37) nextMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, pageNumber);
        else nextMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, pageNumber+1);
        next.setItemMeta(nextMeta);

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
                    String item = minecraftItem.split(":")[1];
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
