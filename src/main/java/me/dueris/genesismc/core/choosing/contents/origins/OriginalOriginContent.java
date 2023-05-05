package me.dueris.genesismc.core.choosing.contents.origins;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemPropertiesMultipleLore;
import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemProperties;
import static org.bukkit.ChatColor.*;

public class OriginalOriginContent {

    public static ItemStack[] HumanContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack human = new ItemStack(Material.PLAYER_HEAD);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack human_origin_info = new ItemStack(Material.FILLED_MAP);
        ItemStack blank = new ItemStack(Material.PAPER);

        human_origin_info = itemProperties(human_origin_info, "Nothing", null, null, WHITE + "Enough said.");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        human = itemProperties(human, "Human", null, Enchantment.ARROW_INFINITE, WHITE + "Human Origin");

        ItemStack[] humangui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, human, air, air, air, air, air, air, blank, blank, human_origin_info, blank, blank, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return humangui_items;
    }

    public static @Nullable ItemStack @NotNull [] AvianContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack avian = new ItemStack(Material.FEATHER);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack avian_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack avian_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack avian_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack avian_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack avian_origin_info5 = new ItemStack(Material.FILLED_MAP);

        avian_origin_info1 = itemProperties(avian_origin_info1, UNDERLINE + "Featherweight", null, null, WHITE + "You fall as gently to the ground as a feather would, unless you shift");
        avian_origin_info2 = itemProperties(avian_origin_info2, UNDERLINE + "Tailwind", null, null, WHITE + "You are a little quicker on foot than others");
        avian_origin_info2 = itemProperties(avian_origin_info2, UNDERLINE + "Oviparous", null, null, WHITE + "Whenever you wake up in the morning, you lay an egg");
        avian_origin_info4 = itemProperties(avian_origin_info4, UNDERLINE + "Vegetarian", null, null, WHITE + "You can't digest any meat");
        avian_origin_info5 = itemProperties(avian_origin_info5, UNDERLINE + "Fresh Air", null, null, WHITE + "When sleeping, your bed needs to be at an altitude of at least 100 blocks");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        avian = itemProperties(avian, "Avian", null, Enchantment.ARROW_INFINITE, DARK_AQUA + "Avian Origin");

        ItemStack[] aviangui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, avian, air, air, air, air, air, air, avian_origin_info1, avian_origin_info2, avian_origin_info3, avian_origin_info4, avian_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return aviangui_items;
    }

    public static @Nullable ItemStack @NotNull [] ArachnidContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack spider = new ItemStack(Material.COBWEB);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack spider_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack spider_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack spider_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack spider_origin_info4 = new ItemStack(Material.FILLED_MAP);

        spider_origin_info1 = itemProperties(spider_origin_info1, UNDERLINE + "SpiderMan", null, null, WHITE + "You can climb up walls, but not when in the rain");
        spider_origin_info2 = itemPropertiesMultipleLore(spider_origin_info2, UNDERLINE + "Weaver", null, null, Arrays.asList(WHITE + "You hinder your foes with", WHITE + "cobwebs upon attacking them"));
        spider_origin_info3 = itemProperties(spider_origin_info3, UNDERLINE + "Squishable", null, null, WHITE + "You have 3 less hearts");
        spider_origin_info4 = itemProperties(spider_origin_info4, UNDERLINE + "Tiny Carnivore", null, null, WHITE + "You can only eat meat");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        spider = itemProperties(spider, "Arachnid", null, Enchantment.ARROW_INFINITE, RED + "Spider Origin");

        ItemStack[] spidergui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, spider, air, air, air, air, air, air, spider_origin_info1, spider_origin_info2, spider_origin_info3, spider_origin_info4, blank, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return spidergui_items;
    }

    public static @Nullable ItemStack @NotNull [] ElytrianContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack elyrtian = new ItemStack(Material.ELYTRA);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack elyrtian_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack elyrtian_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack elyrtian_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack elyrtian_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack elyrtian_origin_info5 = new ItemStack(Material.FILLED_MAP);

        elyrtian_origin_info1 = itemProperties(elyrtian_origin_info1, UNDERLINE + "Winged", null, null, WHITE + "You have Elytra wings without needing to equip any");
        elyrtian_origin_info2 = itemProperties(elyrtian_origin_info2, UNDERLINE + "Gift of the Winds", null, null, WHITE + "Every 60 seconds, you can launch yourself 20 blocks in the air");
        elyrtian_origin_info3 = itemProperties(elyrtian_origin_info3, UNDERLINE + "Claustrophobia", null, null, WHITE + "Being somewhere with a low ceiling for too long will weaken you");
        elyrtian_origin_info4 = itemProperties(elyrtian_origin_info4, UNDERLINE + "Need for Mobility", null, null, WHITE + "You cannot wear any heavy armour with prot values higher than chainmail");
        elyrtian_origin_info5 = itemProperties(elyrtian_origin_info5, UNDERLINE + "Brittle Bones", null, null, WHITE + "You take more damage from falling and flying into blocks");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        elyrtian = itemProperties(elyrtian, "Elytrian", null, Enchantment.ARROW_INFINITE, GRAY + "Elytrian Origin");

        ItemStack[] elyrtiangui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, elyrtian, air, air, air, air, air, air, elyrtian_origin_info1, elyrtian_origin_info2, elyrtian_origin_info3, elyrtian_origin_info4, elyrtian_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return elyrtiangui_items;
    }

    public static @Nullable ItemStack @NotNull [] ShulkContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack shulk = new ItemStack(Material.SHULKER_SHELL);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack shulk_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack shulk_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack shulk_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack shulk_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack shulk_origin_info5 = new ItemStack(Material.FILLED_MAP);

        shulk_origin_info1 = itemPropertiesMultipleLore(shulk_origin_info1, UNDERLINE + "Hoarder" + DARK_GRAY + " (/shulk open, or press the F key to open)", null, null, Arrays.asList(WHITE + "You have 9 extra inventory slots.", WHITE + "You keep these items upon death"));
        shulk_origin_info2 = itemProperties(shulk_origin_info2, UNDERLINE + "Sturdy Skin", null, null, WHITE + "Your skin has natural protection");
        shulk_origin_info3 = itemProperties(shulk_origin_info3, UNDERLINE + "Strong Arms", null, null, WHITE + "You can break natural stones without a pickaxe");
        shulk_origin_info4 = itemProperties(shulk_origin_info4, UNDERLINE + "Unwieldy", null, null, WHITE + "You cannot hold a shield");
        shulk_origin_info5 = itemProperties(shulk_origin_info5, UNDERLINE + "Large Appetite", null, null, WHITE + "You exhaust much quicker than others");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        shulk = itemProperties(shulk, "Shulk", null, Enchantment.ARROW_INFINITE, LIGHT_PURPLE + "Shulker Origin");

        ItemStack[] shulkgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, shulk, air, air, air, air, air, air, shulk_origin_info1, shulk_origin_info2, shulk_origin_info3, shulk_origin_info4, shulk_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return shulkgui_items;
    }

    //Feline

    public static @Nullable ItemStack @NotNull [] EnderianContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack ender = new ItemStack(Material.ENDER_PEARL);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack ender_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack ender_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack ender_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack ender_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack ender_origin_info5 = new ItemStack(Material.FILLED_MAP);

        ender_origin_info1 = itemPropertiesMultipleLore(ender_origin_info1, UNDERLINE + "Teleportaion", null, null, Arrays.asList(WHITE + "You have an infinite ender pearl", WHITE + "that deals no damage"));
        ender_origin_info2 = itemPropertiesMultipleLore(ender_origin_info2, UNDERLINE + "Hydrophobia", null, null, Arrays.asList(WHITE + "You take damage while in", WHITE + "contact with water"));
        ender_origin_info3 = itemProperties(ender_origin_info3, UNDERLINE + "Delicate touch", null, null, WHITE + "You have silk touch hands");
        ender_origin_info4 = itemPropertiesMultipleLore(ender_origin_info4, UNDERLINE + "Brethren of the End", null, null, Arrays.asList(WHITE + "Enderman don't get mad at you", WHITE + "upon looking at them"));
        ender_origin_info5 = itemProperties(ender_origin_info5, UNDERLINE + "Bearer of Pearls", null, null, WHITE + "You always drop 0-2 ender pearls upon death");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        ender = itemProperties(ender, "Enderian", null, Enchantment.ARROW_INFINITE, LIGHT_PURPLE + "Enderman Origin");

        ItemStack[] endergui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, ender, air, air, air, air, air, air, ender_origin_info1, ender_origin_info2, ender_origin_info3, ender_origin_info4, ender_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};
        return endergui_items;
    }

    public static @Nullable ItemStack @NotNull [] MerlingContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack mermaid = new ItemStack(Material.COD);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack mermaid_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack mermaid_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack mermaid_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack mermaid_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack mermaid_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack mermaid_origin_info6 = new ItemStack(Material.FILLED_MAP);

        mermaid_origin_info1 = itemProperties(mermaid_origin_info1, UNDERLINE + "Gills", null, null, WHITE + "You can ONLY breathe underwater, when raining, you can breathe on land for a short time");
        mermaid_origin_info2 = itemProperties(mermaid_origin_info2, UNDERLINE + "Wet Eyes", null, null, WHITE + "Your vision underwater is nearly perfect");
        mermaid_origin_info3 = itemProperties(mermaid_origin_info3, UNDERLINE + "Opposing Forces", null, null, WHITE + "You take significantly more damage from fire");
        mermaid_origin_info4 = itemProperties(mermaid_origin_info4, UNDERLINE + "Fins", null, null, WHITE + "You can swim much faster underwater, and don't sink underwater");
        mermaid_origin_info5 = itemProperties(mermaid_origin_info5, UNDERLINE + "Please don't", null, null, WHITE + "Don't eat fish, its basically cannabalism and thats gross. It gives you nausea.");
        mermaid_origin_info6 = itemProperties(mermaid_origin_info6, UNDERLINE + "Luck of the Sea", null, null, WHITE + "You have increased fishing luck.");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        mermaid = itemProperties(mermaid, "Merling", null, Enchantment.ARROW_INFINITE, BLUE + "Merling Origin");

        ItemStack[] mermaidgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, mermaid, air, air, air, air, air, air, mermaid_origin_info1, mermaid_origin_info2, mermaid_origin_info3, mermaid_origin_info4, mermaid_origin_info5, air, air, air, air, mermaid_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return mermaidgui_items;
    }

    public static @Nullable ItemStack @NotNull [] BlazebornContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack blaze = new ItemStack(Material.BLAZE_POWDER);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack blaze_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack blaze_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack blaze_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack blaze_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack blaze_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack blaze_origin_info6 = new ItemStack(Material.FILLED_MAP);
        ItemStack blaze_origin_info7 = new ItemStack(Material.FILLED_MAP);

        blaze_origin_info1 = itemProperties(blaze_origin_info1, UNDERLINE + "Born from Flames", null, null, WHITE + "Your natural spawn is in the Nether");
        blaze_origin_info2 = itemProperties(blaze_origin_info2, UNDERLINE + "Burning Wrath", null, null, WHITE + "When on fire, you deal additional damage");
        blaze_origin_info3 = itemProperties(blaze_origin_info3, UNDERLINE + "Fire Immunity", null, null, WHITE + "You are immune to all types of fire damage");
        blaze_origin_info4 = itemProperties(blaze_origin_info4, UNDERLINE + "To Hot for.. Uh.. Ya.. Water?", null, null, WHITE + "You damage while in water, and Merlings deal more damage to you");
        blaze_origin_info5 = itemProperties(blaze_origin_info5, UNDERLINE + "Hotblooded", null, null, WHITE + "Due to your hot body, venom burns up, making you immune to poison");
        blaze_origin_info6 = itemProperties(blaze_origin_info6, UNDERLINE + "Opposite Forces", null, null, WHITE + "You are much weaker in colder biomes and at high altitudes");
        blaze_origin_info7 = itemProperties(blaze_origin_info7, UNDERLINE + "Flames of the Nether", null, null, WHITE + "Upon hitting someone, they are set on fire");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        blaze = itemProperties(blaze, "Blazeborn", null, Enchantment.ARROW_INFINITE, GOLD + "Blaze Origin");

        ItemStack[] blazegui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, blaze, air, air, air, air, air, air, blaze_origin_info1, blaze_origin_info2, blaze_origin_info3, blaze_origin_info4, blaze_origin_info5, air, air, air, air, blaze_origin_info6, blaze_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return blazegui_items;
    }

    public static @Nullable ItemStack @NotNull [] PhantomContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack phantom = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack phantom_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack phantom_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack phantom_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack phantom_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack phantom_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack phantom_origin_info6 = new ItemStack(Material.FILLED_MAP);

        phantom_origin_info1 = itemProperties(phantom_origin_info1, UNDERLINE + "Translucent", null, null, WHITE + "You are slightly translucent, and at night you become more solid");
        phantom_origin_info2 = itemProperties(phantom_origin_info2, UNDERLINE + "Not Really a Vampire", null, null, WHITE + "You take damage from sunlight");
        phantom_origin_info3 = itemProperties(phantom_origin_info3, UNDERLINE + "Phasing" + DARK_GRAY + "Press F to activate, or use item", null, null, WHITE + "You can turn into your \"Phantom Form\", allowing you to walk through walls");
        phantom_origin_info4 = itemProperties(phantom_origin_info4, UNDERLINE + "Fast Metabolism", null, null, WHITE + "While in Phantom Form, you loose twice as much hunger");
        phantom_origin_info5 = itemProperties(phantom_origin_info5, UNDERLINE + "Fragile Creature", null, null, WHITE + "You have 3 less hearts");
        phantom_origin_info6 = itemProperties(phantom_origin_info6, UNDERLINE + "Invisibility", null, null, WHITE + "While phantomized, you become fully invisible.");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        phantom = itemProperties(phantom, "Phantom", null, Enchantment.ARROW_INFINITE, BLUE + "Phantom Origin");

        ItemStack[] phantomgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, phantom, air, air, air, air, air, air, phantom_origin_info1, phantom_origin_info2, phantom_origin_info3, phantom_origin_info4, phantom_origin_info5, air, air, air, air, phantom_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return phantomgui_items;
    }
}
