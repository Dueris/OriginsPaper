package me.dueris.genesismc.screen;

import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChooseEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.util.SendCharts;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.bukkit.Bukkit.getServer;

public class ScreenConstants {
    public static HashMap<Integer/*page number*/, List<Origin>/*origins on that page*/> pages = new HashMap<>();

    public static void splitPages() {
        Registrar<Origin> registry = GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN);
        List<Origin> sortedOrigins = registry.rawRegistry.values().stream()
            .sorted(Comparator.comparingInt(Origin::getOrder))
            .filter(origin -> !CraftApoli.isCoreOrigin(origin))
            .filter(origin -> !origin.getUnchooseable())
            .toList();
        AtomicInteger pageNumber = new AtomicInteger();
        AtomicInteger index = new AtomicInteger();
        sortedOrigins.forEach(origin -> {
            if (index.get() < 35) {
                pageNumber.getAndIncrement();
                index.set(0);
            } else {
                index.getAndIncrement();
            }
            if (pages.containsKey(pageNumber.get())) {
                pages.get(pageNumber.get()).add(origin);
            } else {
                pages.put(pageNumber.get(), new ArrayList<>() {{
                    add(origin);
                }});
            }
        });
    }

    public static void DefaultChoose(Player p) {
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

        //default choose
        p.closeInventory();

        OriginChooseEvent chooseEvent = new OriginChooseEvent(p);
        getServer().getPluginManager().callEvent(chooseEvent);

        SendCharts.originPopularity(p);

    }

    public static void setAttributesToDefault(Player p) {
        p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
        p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
        p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);
        p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
        p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
        p.getAttribute(Attribute.GENERIC_LUCK).setBaseValue(0);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.10000000149011612F);
    }

    public static ItemStack itemProperties(ItemStack item, String displayName, ItemFlag itemFlag, Enchantment enchantment, String lore) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);
        if (itemFlag != null) itemMeta.addItemFlags(itemFlag);
        if (enchantment != null) itemMeta.addEnchant(enchantment, 1, true);
        if (lore != null) {
            ArrayList<String> itemLore = new ArrayList<>();
            itemLore.add(lore);
            itemMeta.setLore(itemLore);
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack itemPropertiesMultipleLore(ItemStack item, String displayName, ItemFlag itemFlag, Enchantment enchantment, List lore) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);
        if (itemFlag != null) itemMeta.addItemFlags(itemFlag);
        if (enchantment != null) itemMeta.addEnchant(enchantment, 1, true);
        if (lore != null) itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static List<String> cutStringIntoLines(String string) {
        ArrayList<String> strings = new ArrayList<>();
        int startStringLength = string.length();
        while (string.length() > 40) {
            for (int i = 40; i > 1; i--) {
                if (String.valueOf(string.charAt(i)).matches("[\\s\\n]") || String.valueOf(string.charAt(i)).equals(" ")) {
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
}
