package me.dueris.genesismc.core.choosing.contents;

import me.dueris.genesismc.core.files.GenesisDataFiles;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemProperties;
import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.RED;

public class MainMenuContents {

    public static @Nullable ItemStack @NotNull [] GenesisMainMenuContents(Player p){

        ItemStack human = new ItemStack(Material.PLAYER_HEAD);
        ItemStack enderian = new ItemStack(Material.ENDER_PEARL);
        ItemStack shulk = new ItemStack(Material.SHULKER_SHELL);
        ItemStack arachnid = new ItemStack(Material.COBWEB);
        ItemStack creep = new ItemStack(Material.GUNPOWDER);
        ItemStack phantom = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemStack slimeling = new ItemStack(Material.SLIME_BALL);
        ItemStack feline = new ItemStack(Material.ORANGE_WOOL);
        ItemStack blazeborn = new ItemStack(Material.BLAZE_POWDER);
        ItemStack starborne = new ItemStack(Material.NETHER_STAR);
        ItemStack merling = new ItemStack(Material.COD);
        ItemStack allay = new ItemStack(Material.AMETHYST_SHARD);
        ItemStack rabbit = new ItemStack(Material.CARROT);
        ItemStack bumblebee = new ItemStack(Material.HONEYCOMB);
        ItemStack elytrian = new ItemStack(Material.ELYTRA);
        ItemStack avian = new ItemStack(Material.FEATHER);
        ItemStack piglin = new ItemStack(Material.GOLD_INGOT);
        ItemStack sculkling = new ItemStack(Material.ECHO_SHARD);
        ItemStack blank = new ItemStack(Material.AIR);
        ItemStack custom_originmenu = new ItemStack(Material.TIPPED_ARROW);
        ItemStack bars = new ItemStack(Material.IRON_BARS);
        ItemStack random = new ItemStack(Material.MAGMA_CREAM);
        ItemStack description = new ItemStack(Material.AIR);

        ItemMeta meta = random.getItemMeta();
        random.setItemMeta(meta);

        if (GenesisDataFiles.getPlugCon().getString("human-disable").equalsIgnoreCase("false")) {
            SkullMeta skull_p = (SkullMeta) human.getItemMeta();
            skull_p.setOwningPlayer(p);
            skull_p.setOwner(p.getName());
            skull_p.setPlayerProfile(p.getPlayerProfile());
            skull_p.setOwnerProfile(p.getPlayerProfile());
            human.setItemMeta(skull_p);
            human = itemProperties(human, WHITE + "Human", null, null, WHITE + "A normal Minecraftexperience");
        } else {
            human = itemProperties(human, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("enderian-disable").equalsIgnoreCase("false")) {
            enderian = itemProperties(enderian, LIGHT_PURPLE + "Enderian", null, null, WHITE + "Born as the children of the Ender Dragon, Enderians are capable of teleporting, but are vulnerable to water");
        } else {
            enderian = itemProperties(enderian, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("shulk-disable").equalsIgnoreCase("false")) {
            shulk = itemProperties(shulk, DARK_PURPLE + "Shulk", null, null, WHITE + "Related to Shulkers, the bodies of the Shulk are outfitted with a protective shell-like skin and have an extra inventory");
        } else {
            shulk = itemProperties(shulk, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("arachnid-disable").equalsIgnoreCase("false")) {
            arachnid = itemProperties(arachnid, RED + "Arachnid", null, null, WHITE + "Their climbing abilities and the ability to trap their foes in a spiderweb make the Arachnid perfect hunters");
        } else {
            arachnid = itemProperties(arachnid, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("creep-disable").equalsIgnoreCase("false")) {
            creep = itemProperties(creep, GREEN + "Creep", null, null, WHITE + "Silent but deadly, the Creep are skilled in the arts of stealth,  but are TERRIBLY allergic to cats");
        } else {
            creep = itemProperties(creep, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("phantom-disable").equalsIgnoreCase("false")) {
            phantom = itemProperties(phantom, BLUE + "Phantom", null, null, WHITE + "As half-human and half-phantom beings, these creatures can switch between a Phantom and a normal form");
        } else {
            phantom = itemProperties(phantom, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("slimeling-disable").equalsIgnoreCase("false")) {
            slimeling = itemProperties(slimeling, GREEN + "Slimeling", null, null, WHITE + "not coded yet");
        } else {
            slimeling = itemProperties(slimeling, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("feline-disable").equalsIgnoreCase("false")) {
            feline = itemProperties(feline, AQUA + "Feline", ItemFlag.HIDE_ATTRIBUTES, null, WHITE + "not coded yet");
        } else {
            feline = itemProperties(feline, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("blazeborn-disable").equalsIgnoreCase("false")) {
            blazeborn = itemProperties(blazeborn, GOLD + "Blazeborn", null, null, WHITE + "Late descendants of the Blaze,  the Blazeborn are naturally immune to the perils of the Nether");
        } else {
            blazeborn = itemProperties(blazeborn, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("starborne-disable").equalsIgnoreCase("false")) {
            starborne = itemProperties(starborne, LIGHT_PURPLE + "Starborne", null, null, WHITE + "not coded yet");
        } else {
            starborne = itemProperties(starborne, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("merling-disable").equalsIgnoreCase("false")) {
            merling = itemProperties(merling, BLUE + "Merling", null, null, WHITE + "not coded yet");
        } else {
            merling = itemProperties(merling, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("allay-disable").equalsIgnoreCase("false")) {
            allay = itemProperties(allay, AQUA + "Allay", null, null, WHITE + "not coded yet");
        } else {
            allay = itemProperties(allay, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("rabbit-disable").equalsIgnoreCase("false")) {
            rabbit = itemProperties(rabbit, GOLD + "Rabbit", null, null, WHITE + "These furry bunnies are extremly good jumpers and have amazing agility");
        } else {
            rabbit = itemProperties(rabbit, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("bumblebee-disable").equalsIgnoreCase("false")) {
            bumblebee = itemProperties(bumblebee, YELLOW + "Bumblebee", null, null, WHITE + "not coded yet");
        } else {
            bumblebee = itemProperties(bumblebee, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("elytrian-disable").equalsIgnoreCase("false")) {
            elytrian = itemProperties(elytrian, GRAY + "Elytrian", null, null, WHITE + "not coded yet");
        } else {
            elytrian = itemProperties(elytrian, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("avian-disable").equalsIgnoreCase("false")) {
            avian = itemProperties(avian, DARK_AQUA + "Avian", null, null, WHITE + "The Avian race has lost their ability to fly a long time ago. Now these peaceful creatures can be seen gliding from one place to another");
        } else {
            avian = itemProperties(avian, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("piglin-disable").equalsIgnoreCase("false")) {
            piglin = itemProperties(piglin, GOLD + "Piglin", null, null, WHITE + "These evolved pigs love gold and shiny things. They have adapted to the harsh environments of the Nether and so they are weaker in other environments");
        } else {
            piglin = itemProperties(piglin, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getPlugCon().getString("sculkling-disable").equalsIgnoreCase("false")) {
            sculkling = itemProperties(sculkling, BLUE + "Sculkling", null, null, WHITE + "not coded yet");
        } else {
            sculkling = itemProperties(sculkling, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }

        random = itemProperties(random, ChatColor.LIGHT_PURPLE + "Orb of Origins", ItemFlag.HIDE_ENCHANTS, null, null);
        custom_originmenu = itemProperties(custom_originmenu, ChatColor.YELLOW + "Custom Origins", ItemFlag.HIDE_ENCHANTS, null, null);
        bars = itemProperties(bars, "", ItemFlag.HIDE_ENCHANTS, null, null);
        
        ItemStack[] mainmenucontents = {enderian, merling, phantom, elytrian, blazeborn, avian, arachnid, shulk, feline,
                description, description, description, description, description, description, description, description, description,
                bars, bars, bars, bars, bars, bars, bars, bars, bars,
                starborne, allay, rabbit, bumblebee, human , sculkling, creep, slimeling, piglin,
                description, description, description, description, description, description, description, description, description,
                blank, blank, blank, blank, custom_originmenu, blank, blank, blank, blank};

        return mainmenucontents;

    }

}
