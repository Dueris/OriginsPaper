package me.dueris.genesismc.core.choosing.contents;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemProperties;
import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemPropertiesMultipleLore;
import static me.dueris.genesismc.core.choosing.ChoosingCUSTOM.cutStringIntoLists;
import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.*;

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
        ItemStack custom_originmenu = new ItemStack(Material.TIPPED_ARROW);
        ItemStack random = new ItemStack(Material.MAGMA_CREAM);
        ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemStack close = new ItemStack(Material.BARRIER);


        ItemMeta meta = random.getItemMeta();
        random.setItemMeta(meta);

        if (GenesisDataFiles.getMainConfig().getString("human-disable").equalsIgnoreCase("false")) {
            SkullMeta skull_p = (SkullMeta) human.getItemMeta();
            skull_p.setOwningPlayer(p);
            skull_p.setOwner(p.getName());
            skull_p.setPlayerProfile(p.getPlayerProfile());
            skull_p.setOwnerProfile(p.getPlayerProfile());
            human.setItemMeta(skull_p);
            human = itemProperties(human, WHITE + "Human", null, null, WHITE + "A normal Minecraft experience.");
        } else {
            human = itemProperties(human, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("enderian-disable").equalsIgnoreCase("false")) {
            enderian = itemPropertiesMultipleLore(enderian, LIGHT_PURPLE + "Enderian", null, null, cutStringIntoLists("Born as the children of the Ender Dragon, Enderians are capable of teleporting, but are vulnerable to water."));
        } else {
            enderian = itemProperties(enderian, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("shulk-disable").equalsIgnoreCase("false")) {
            shulk = itemPropertiesMultipleLore(shulk, DARK_PURPLE + "Shulk", null, null, cutStringIntoLists("Related to Shulkers, the bodies of the Shulk are outfitted with a protective shell-like skin and have an extra inventory."));
        } else {
            shulk = itemProperties(shulk, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("arachnid-disable").equalsIgnoreCase("false")) {
            arachnid = itemPropertiesMultipleLore(arachnid, RED + "Arachnid", null, null, cutStringIntoLists("Their climbing abilities and the ability to trap their foes in a spiderweb make the Arachnid perfect hunters."));
        } else {
            arachnid = itemProperties(arachnid, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("creep-disable").equalsIgnoreCase("false")) {
            creep = itemPropertiesMultipleLore(creep, GREEN + "Creep", null, null, cutStringIntoLists("Silent but deadly, the Creep are skilled in the arts of stealth, however they are TERRIBLY allergic to cats."));
        } else {
            creep = itemProperties(creep, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("phantom-disable").equalsIgnoreCase("false")) {
            phantom = itemPropertiesMultipleLore(phantom, BLUE + "Phantom", null, null, cutStringIntoLists("As half-human and half-phantom beings, these creatures can switch between a Phantom and a normal form."));
        } else {
            phantom = itemProperties(phantom, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner");
        }
        if (GenesisDataFiles.getMainConfig().getString("slimeling-disable").equalsIgnoreCase("false")) {
            slimeling = itemProperties(slimeling, GREEN + "Slimeling", null, null, RED + "Not yet implemented.");
        } else {
            slimeling = itemProperties(slimeling, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("feline-disable").equalsIgnoreCase("false")) {
            feline = itemProperties(feline, AQUA + "Feline", ItemFlag.HIDE_ATTRIBUTES, null, RED + "Not yet implemented.");
        } else {
            feline = itemProperties(feline, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("blazeborn-disable").equalsIgnoreCase("false")) {
            blazeborn = itemPropertiesMultipleLore(blazeborn, GOLD + "Blazeborn", null, null, cutStringIntoLists("Late descendants of the Blaze,  the Blazeborn are naturally immune to the perils of the Nether."));
        } else {
            blazeborn = itemProperties(blazeborn, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("starborne-disable").equalsIgnoreCase("false")) {
            starborne = itemProperties(starborne, LIGHT_PURPLE + "Starborne", null, null, RED + "Not yet implemented.");
        } else {
            starborne = itemProperties(starborne, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("merling-disable").equalsIgnoreCase("false")) {
            merling = itemProperties(merling, BLUE + "Merling", null, null, RED + "Not yet implemented.");
        } else {
            merling = itemProperties(merling, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("allay-disable").equalsIgnoreCase("false")) {
            allay = itemProperties(allay, AQUA + "Allay", null, null, RED + "Not yet implemented.");
        } else {
            allay = itemProperties(allay, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("rabbit-disable").equalsIgnoreCase("false")) {
            rabbit = itemPropertiesMultipleLore(rabbit, GOLD + "Rabbit", null, null, cutStringIntoLists("These furry bunnies are extremly good jumpers and have amazing agility."));
        } else {
            rabbit = itemProperties(rabbit, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("bumblebee-disable").equalsIgnoreCase("false")) {
            bumblebee = itemProperties(bumblebee, YELLOW + "Bumblebee", null, null, RED + "Not yet implemented.");
        } else {
            bumblebee = itemProperties(bumblebee, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("elytrian-disable").equalsIgnoreCase("false")) {
            elytrian = itemPropertiesMultipleLore(elytrian, GRAY + "Elytrian", null, null, cutStringIntoLists("Often flying around in the winds, Elytrians are uncomfortable when they don't have enough space above their head."));
        } else {
            elytrian = itemProperties(elytrian, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("avian-disable").equalsIgnoreCase("false")) {
            avian = itemPropertiesMultipleLore(avian, DARK_AQUA + "Avian", null, null, cutStringIntoLists("The Avian race has lost their ability to fly a long time ago. Now these peaceful creatures can be seen gliding from one place to another."));
        } else {
            avian = itemProperties(avian, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("piglin-disable").equalsIgnoreCase("false")) {
            piglin = itemPropertiesMultipleLore(piglin, GOLD + "Piglin", null, null, cutStringIntoLists("These evolved pigs love gold and shiny things. They have adapted to the harsh environments of the Nether and so they are weaker in other environments."));
        } else {
            piglin = itemProperties(piglin, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }
        if (GenesisDataFiles.getMainConfig().getString("sculkling-disable").equalsIgnoreCase("false")) {
            sculkling = itemProperties(sculkling, BLUE + "Sculkling", null, null, RED + "Not yet implemented.");
        } else {
            sculkling = itemProperties(sculkling, RED + "Unavailable", ItemFlag.HIDE_ENCHANTS, Enchantment.ARROW_INFINITE, RED + "This origin is locked by the server owner.");
        }

        random = itemProperties(random, ChatColor.LIGHT_PURPLE + "Orb of Origins", ItemFlag.HIDE_ENCHANTS, null, null);
        custom_originmenu = itemProperties(custom_originmenu, ChatColor.YELLOW + "Custom Origins", ItemFlag.HIDE_ENCHANTS, null, null);
        close = itemProperties(close, RED + "Close" , null, null, RED + "Cancel choosing");
        ItemStack randomOrb = itemProperties(orb.clone(), LIGHT_PURPLE + "Random", null, null, null);
        NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "orb");
        ItemMeta randomOrbmeta = randomOrb.getItemMeta();
        randomOrbmeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "orb");
        randomOrb.setItemMeta(randomOrbmeta);

        ItemStack[] mainmenucontents = {avian, arachnid, elytrian, shulk, feline, enderian, merling, blazeborn, phantom,
                background, background, background, background, human, background, background, background, background,
                filler, filler, filler, filler, filler, filler, filler, filler, filler,
                starborne, allay, rabbit, bumblebee, background , sculkling, creep, slimeling, piglin,
                background, background, background, background, background, background, background, background, background,
                filler, filler, filler, randomOrb, close, custom_originmenu, filler, filler, filler};

        return mainmenucontents;

    }

}
