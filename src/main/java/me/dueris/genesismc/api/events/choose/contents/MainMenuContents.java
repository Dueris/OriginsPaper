package me.dueris.genesismc.api.events.choose.contents;

import me.dueris.genesismc.core.files.GenesisDataFiles;
import me.dueris.genesismc.core.items.OrbOfOrigins;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.RED;

public class MainMenuContents {

    public static @Nullable ItemStack @NotNull [] GenesisMainMenuContents(){

        ItemStack human = new ItemStack(Material.PLAYER_HEAD);
        ItemStack enderian = new ItemStack(Material.ENDER_PEARL);
        ItemStack shulk = new ItemStack(Material.SHULKER_SHELL);
        ItemStack arachnid = new ItemStack(Material.COBWEB);
        ItemStack creep = new ItemStack(Material.GUNPOWDER);
        ItemStack phantom = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemStack slimeling = new ItemStack(Material.SLIME_BALL);
        ItemStack vexian = new ItemStack(Material.IRON_SWORD);
        ItemStack blazeborn = new ItemStack(Material.BLAZE_POWDER);
        ItemStack starborne = new ItemStack(Material.NETHER_STAR);
        ItemStack merling = new ItemStack(Material.COD);
        ItemStack allay = new ItemStack(Material.AMETHYST_SHARD);
        ItemStack rabbit = new ItemStack(Material.CARROT);
        ItemStack bumblebee = new ItemStack(Material.HONEYCOMB);
        ItemStack elytrian = new ItemStack(Material.ELYTRA);
        ItemStack avian = new ItemStack(Material.FEATHER);
        ItemStack piglin = new ItemStack(Material.GOLD_INGOT);
        ItemStack dragonborne = new ItemStack(Material.DRAGON_BREATH);
        ItemStack blank = new ItemStack(Material.AIR);
        ItemStack custom_originmenu = new ItemStack(Material.TIPPED_ARROW);
        ItemStack expanded = new ItemStack(Material.MUSIC_DISC_OTHERSIDE);
        ItemStack description = new ItemStack(Material.MAP);
        ItemStack bars = new ItemStack(Material.IRON_BARS);
        ItemStack random = OrbOfOrigins.orb;

        if (GenesisDataFiles.getPlugCon().getString("human-disable").equalsIgnoreCase("false")) {
            ItemMeta human_meta = human.getItemMeta();
            human_meta.setDisplayName(WHITE + "Human");
            ArrayList<String> human_lore = new ArrayList<>();
            human_lore.add(WHITE + "Human Origin");
            human_meta.setLore(human_lore);
            human.setItemMeta(human_meta);
        } else {
            ItemMeta nope_meta = human.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            human.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("enderian-disable").equalsIgnoreCase("false")) {
            ItemMeta ender_meta = enderian.getItemMeta();
            ender_meta.setDisplayName(LIGHT_PURPLE + "Enderian");
            ArrayList<String> ender_lore = new ArrayList<>();
            ender_lore.add(WHITE + "Enderman Origin");
            ender_meta.setLore(ender_lore);
            enderian.setItemMeta(ender_meta);
        } else {
            ItemMeta nope_meta = enderian.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            enderian.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("shulk-disable").equalsIgnoreCase("false")) {
            ItemMeta shulk_meta = shulk.getItemMeta();
            shulk_meta.setDisplayName(DARK_PURPLE + "Shulk");
            ArrayList<String> shulk_lore = new ArrayList<>();
            shulk_lore.add(WHITE + "shulk Origin");
            shulk_meta.setLore(shulk_lore);
            shulk.setItemMeta(shulk_meta);
        } else {
            ItemMeta nope_meta = shulk.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            shulk.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("arachnid-disable").equalsIgnoreCase("false")) {
            ItemMeta spider_meta = arachnid.getItemMeta();
            spider_meta.setDisplayName(RED + "Arachnid");
            ArrayList<String> spider_lore = new ArrayList<>();
            spider_lore.add(WHITE + "Spider Origin");
            spider_meta.setLore(spider_lore);
            arachnid.setItemMeta(spider_meta);
        } else {
            ItemMeta nope_meta = arachnid.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            arachnid.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("creep-disable").equalsIgnoreCase("false")) {
            ItemMeta creep_meta = creep.getItemMeta();
            creep_meta.setDisplayName(GREEN + "Creep");
            ArrayList<String> creep_lore = new ArrayList<>();
            creep_lore.add(WHITE + "Creeper Origin");
            creep_meta.setLore(creep_lore);
            creep.setItemMeta(creep_meta);
        } else {
            ItemMeta nope_meta = creep.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            creep.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("phantom-disable").equalsIgnoreCase("false")) {
            ItemMeta phantom_meta = phantom.getItemMeta();
            phantom_meta.setDisplayName(BLUE + "Phantom");
            ArrayList<String> phantom_lore = new ArrayList<>();
            phantom_lore.add(WHITE + "Phantom Origin");
            phantom_meta.setLore(phantom_lore);
            phantom.setItemMeta(phantom_meta);
        } else {
            ItemMeta nope_meta = phantom.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            phantom.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("slimeling-disable").equalsIgnoreCase("false")) {
            ItemMeta slime_meta = slimeling.getItemMeta();
            slime_meta.setDisplayName(GREEN + "Slimeling");
            ArrayList<String> slime_lore = new ArrayList<>();
            slime_lore.add(WHITE + "Slime Origin");
            slime_meta.setLore(slime_lore);
            slimeling.setItemMeta(slime_meta);
        } else {
            ItemMeta nope_meta = slimeling.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            slimeling.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("vexian-disable").equalsIgnoreCase("false")) {
            ItemMeta vex_meta = vexian.getItemMeta();
            vex_meta.setDisplayName(AQUA + "Vexian");
            ArrayList<String> vex_lore = new ArrayList<>();
            vex_lore.add(WHITE + "Vex Origin");
            vex_meta.getItemFlags().add(ItemFlag.HIDE_ATTRIBUTES);
            vex_meta.setLore(vex_lore);
            vexian.setItemMeta(vex_meta);
        } else {
            ItemMeta nope_meta = vexian.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            vexian.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("blazeborn-disable").equalsIgnoreCase("false")) {
            ItemMeta blaze_meta = blazeborn.getItemMeta();
            blaze_meta.setDisplayName(GOLD + "Blazeborn");
            ArrayList<String> blaze_lore = new ArrayList<>();
            blaze_lore.add(WHITE + "Blaze Origin");
            blaze_meta.setLore(blaze_lore);
            blazeborn.setItemMeta(blaze_meta);
        } else {
            ItemMeta nope_meta = blazeborn.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            blazeborn.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("starborne-disable").equalsIgnoreCase("false")) {
            ItemMeta star_meta = starborne.getItemMeta();
            star_meta.setDisplayName(LIGHT_PURPLE + "Starborne");
            ArrayList<String> star_lore = new ArrayList<>();
            star_lore.add(WHITE + "Starborne Origin");
            star_meta.setLore(star_lore);
            starborne.setItemMeta(star_meta);
        } else {
            ItemMeta nope_meta = starborne.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            starborne.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("merling-disable").equalsIgnoreCase("false")) {
            ItemMeta mer_meta = merling.getItemMeta();
            mer_meta.setDisplayName(BLUE + "Merling");
            ArrayList<String> mer_lore = new ArrayList<>();
            mer_lore.add(WHITE + "Merling Origin");
            mer_meta.setLore(mer_lore);
            merling.setItemMeta(mer_meta);
        } else {
            ItemMeta nope_meta = merling.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            merling.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("allay-disable").equalsIgnoreCase("false")) {
            ItemMeta allay_meta = allay.getItemMeta();
            allay_meta.setDisplayName(AQUA + "Allay");
            ArrayList<String> allay_lore = new ArrayList<>();
            allay_lore.add(WHITE + "Allay Origin");
            allay_meta.setLore(allay_lore);
            allay.setItemMeta(allay_meta);
        } else {
            ItemMeta nope_meta = allay.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            allay.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("rabbit-disable").equalsIgnoreCase("false")) {
            ItemMeta rabbit_meta = rabbit.getItemMeta();
            rabbit_meta.setDisplayName(GOLD + "Rabbit");
            ArrayList<String> rabbit_lore = new ArrayList<>();
            rabbit_lore.add(WHITE + "Rabbit Origin");
            rabbit_meta.setLore(rabbit_lore);
            rabbit.setItemMeta(rabbit_meta);
        } else {
            ItemMeta nope_meta = rabbit.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            rabbit.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("bumblebee-disable").equalsIgnoreCase("false")) {
            ItemMeta bee_meta = bumblebee.getItemMeta();
            bee_meta.setDisplayName(YELLOW + "Bumblebee");
            ArrayList<String> bee_lore = new ArrayList<>();
            bee_lore.add(WHITE + "Bee Origin");
            bee_meta.setLore(bee_lore);
            bumblebee.setItemMeta(bee_meta);
        } else {
            ItemMeta nope_meta = bumblebee.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            bumblebee.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("elytrian-disable").equalsIgnoreCase("false")) {
            ItemMeta elytra_meta = elytrian.getItemMeta();
            elytra_meta.setDisplayName(GRAY + "Elytrian");
            ArrayList<String> elytra_lore = new ArrayList<>();
            elytra_lore.add(WHITE + "Elytra Origin");
            elytra_meta.setLore(elytra_lore);
            elytrian.setItemMeta(elytra_meta);
        } else {
            ItemMeta nope_meta = elytrian.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            elytrian.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("avian-disable").equalsIgnoreCase("false")) {
            ItemMeta avian_meta = avian.getItemMeta();
            avian_meta.setDisplayName(DARK_AQUA + "Avian");
            ArrayList<String> avian_lore = new ArrayList<>();
            avian_lore.add(WHITE + "Avian Origin");
            avian_meta.setLore(avian_lore);
            avian.setItemMeta(avian_meta);
        } else {
            ItemMeta nope_meta = avian.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            avian.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("piglin-disable").equalsIgnoreCase("false")) {
            ItemMeta pig_meta = piglin.getItemMeta();
            pig_meta.setDisplayName(GOLD + "Piglin");
            ArrayList<String> pig_lore = new ArrayList<>();
            pig_lore.add(WHITE + "Piglin Origin");
            pig_meta.setLore(pig_lore);
            piglin.setItemMeta(pig_meta);
        } else {
            ItemMeta nope_meta = piglin.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            piglin.setItemMeta(nope_meta);
        }
        if (GenesisDataFiles.getPlugCon().getString("dragonborne-disable").equalsIgnoreCase("false")) {
            ItemMeta dragonborne_meta = dragonborne.getItemMeta();
            dragonborne_meta.setDisplayName(DARK_PURPLE + "Dragonborne");
            ArrayList<String> dragonborne_lore = new ArrayList<>();
            dragonborne_lore.add(WHITE + "Dragon Origin");
            dragonborne_meta.setLore(dragonborne_lore);
            dragonborne.setItemMeta(dragonborne_meta);
        } else {
            ItemMeta nope_meta = dragonborne.getItemMeta();
            nope_meta.setDisplayName(RED + "Unavailable");
            nope_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            nope_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ArrayList<String> nope_lore = new ArrayList<>();
            nope_lore.add(RED + "This origin is locked by the server owner");
            nope_meta.setLore(nope_lore);
            dragonborne.setItemMeta(nope_meta);
        }

        ItemMeta ranmeta = random.getItemMeta();
        ranmeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Random Origin");
        ranmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> ranlore = new ArrayList<>();
        ranmeta.setLore(ranlore);
        random.setItemMeta(ranmeta);

        ItemMeta exmeta = expanded.getItemMeta();
        exmeta.setDisplayName(ChatColor.BLUE + "Origins Expanded");
        exmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> explore = new ArrayList<>();
        exmeta.setLore(explore);
        expanded.setItemMeta(exmeta);

        ItemMeta cometa = custom_originmenu.getItemMeta();
        cometa.setDisplayName(ChatColor.YELLOW + "Custom Origins");
        cometa.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> colore = new ArrayList<>();
        cometa.setLore(colore);
        custom_originmenu.setItemMeta(cometa);

        ItemMeta barmeta = bars.getItemMeta();
        barmeta.setDisplayName("");
        barmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> barlore = new ArrayList<>();
        barmeta.setLore(barlore);
        bars.setItemMeta(barmeta);


        ItemStack[] mainmenucontents = {human, enderian, shulk, arachnid, creep, phantom, slimeling, vexian, blazeborn,
                description, description, description, description, description, description, description, description, description,
                bars, bars, bars, bars, bars, bars, bars, bars, bars,
                starborne, merling, allay, rabbit, bumblebee, elytrian, avian, piglin, dragonborne,
                description, description, description, description, description, description, description, description, description,
                blank, blank, blank, expanded, random, custom_originmenu, blank, blank, blank};

        return mainmenucontents;

    }
}
