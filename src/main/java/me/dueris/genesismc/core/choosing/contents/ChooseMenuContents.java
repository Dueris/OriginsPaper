package me.dueris.genesismc.core.choosing.contents;

import me.dueris.genesismc.core.api.factory.CustomOriginAPI;
import me.dueris.genesismc.core.GenesisMC;
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

    public static @Nullable ItemStack @NotNull [] ChooseMenuContent() {
        ItemStack bars = new ItemStack(Material.IRON_BARS);
        ItemStack back = new ItemStack(Material.ARROW);
        ItemStack next = new ItemStack(Material.ARROW);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);

        bars = itemProperties(bars, "", ItemFlag.HIDE_ENCHANTS, null, null);
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        back = itemProperties(back, "Back", ItemFlag.HIDE_ENCHANTS, null, null);
        next = itemProperties(next, "Next", ItemFlag.HIDE_ENCHANTS, null, null);

        ArrayList<ItemStack> contents = new ArrayList<>();
        ArrayList<String> customOriginTags = CustomOriginAPI.getCustomOriginTags();

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
                    while (CustomOriginAPI.getCustomOriginUnChoosable(origintag)) {
                        customOriginTags.remove(0);
                        origintag = customOriginTags.get(0);
                    }
                    String minecraftItem = CustomOriginAPI.getCustomOriginIcon(origintag);
                    String item = minecraftItem.split(":")[1];
                    ItemStack originIcon = new ItemStack(Material.valueOf(item.toUpperCase()));

                    ItemMeta originIconmeta = originIcon.getItemMeta();
                    originIconmeta.setDisplayName(CustomOriginAPI.getCustomOriginName(origintag));
                    originIconmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    originIconmeta.setLore(cutStringIntoLists(CustomOriginAPI.getCustomOriginDescription(origintag)));
                    NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originTag");
                    originIconmeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, origintag);
                    originIcon.setItemMeta(originIconmeta);
                    contents.add(originIcon);
                    customOriginTags.remove(0);
                } else {
                    contents.add(new ItemStack(Material.AIR));
                }
            }
        }

        return contents.toArray(new ItemStack[0]);

    }
}
