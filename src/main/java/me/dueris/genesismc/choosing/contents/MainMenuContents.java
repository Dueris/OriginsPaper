package me.dueris.genesismc.choosing.contents;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import static me.dueris.genesismc.choosing.ChoosingCustomOrigins.cutStringIntoLines;
import static me.dueris.genesismc.choosing.ChoosingMain.*;
import static me.dueris.genesismc.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.*;

public class MainMenuContents {

    private static ItemStack applyProperties(ItemStack icon) {
        for (OriginContainer origin : CraftApoli.getCoreOrigins()) {
            if (origin.getMaterialIcon() != icon.getType()) continue;
            ItemMeta meta = icon.getItemMeta();
            NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originTag");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, origin.getTag());
            icon.setItemMeta(meta);
        }
        return icon;
    }

    public static @Nullable ItemStack @NotNull [] GenesisMainMenuContents(Player p) {

        //puts all loaded the core origins into the hashmap
        HashMap<String, String> originDetails = new HashMap<>();
        for (OriginContainer origin : CraftApoli.getCoreOrigins()) {
            if (!choosing.get(p).getOrigins().contains(origin.getTag())) continue;
            originDetails.put(origin.getTag(), origin.getName());
        }

        //if (originDetails.isEmpty()) return ChooseMenuContents.ChooseMenuContent(0, choosing.get(p));

        HashMap<String, String> originDescriptions = new HashMap<>();
        for (OriginContainer origin : CraftApoli.getCoreOrigins()) {
            originDescriptions.put(origin.getTag(), origin.getDescription());
        }

        //sets up all the items
        ItemStack human = applyProperties(new ItemStack(Material.PLAYER_HEAD));
        ItemStack enderian = applyProperties(new ItemStack(Material.ENDER_PEARL));
        ItemStack shulk = applyProperties(new ItemStack(Material.SHULKER_SHELL));
        ItemStack arachnid = applyProperties(new ItemStack(Material.COBWEB));
        ItemStack creep = applyProperties(new ItemStack(Material.GUNPOWDER));
        ItemStack phantom = applyProperties(new ItemStack(Material.PHANTOM_MEMBRANE));
        ItemStack slimeling = applyProperties(new ItemStack(Material.SLIME_BALL));
        ItemStack feline = applyProperties(new ItemStack(Material.ORANGE_WOOL));
        ItemStack blazeborn = applyProperties(new ItemStack(Material.BLAZE_POWDER));
        ItemStack starborne = applyProperties(new ItemStack(Material.NETHER_STAR));
        ItemStack merling = applyProperties(new ItemStack(Material.COD));
        ItemStack allay = applyProperties(new ItemStack(Material.COOKIE));
        ItemStack rabbit = applyProperties(new ItemStack(Material.CARROT));
        ItemStack bumblebee = applyProperties(new ItemStack(Material.HONEYCOMB));
        ItemStack elytrian = applyProperties(new ItemStack(Material.ELYTRA));
        ItemStack avian = applyProperties(new ItemStack(Material.FEATHER));
        ItemStack piglin = applyProperties(new ItemStack(Material.GOLD_INGOT));
        ItemStack sculkling = applyProperties(new ItemStack(Material.ECHO_SHARD));
        ItemStack custom_originmenu = applyProperties(new ItemStack(Material.TIPPED_ARROW));
        ItemStack background = applyProperties(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        ItemStack filler = applyProperties(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        ItemStack close = applyProperties(new ItemStack(Material.BARRIER));

        //makes human icon appear as own playerhead
        SkullMeta skull_p = (SkullMeta) human.getItemMeta();
        skull_p.setOwningPlayer(p);
        skull_p.setOwner(p.getName());
        skull_p.setPlayerProfile(p.getPlayerProfile());
        skull_p.setOwnerProfile(p.getPlayerProfile());
        human.setItemMeta(skull_p);

        //checks which origins are loaded
        String notFound = LangConfig.getLocalizedString(p, "menu.mainChoose.originNotFound");

        if (originDetails.containsKey("origins:human"))
            human = itemPropertiesMultipleLore(human, WHITE + originDetails.get("origins:human"), null, null, cutStringIntoLines(originDescriptions.get("origins:human")));
        else human = itemProperties(human, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:enderian"))
            enderian = itemPropertiesMultipleLore(enderian, LIGHT_PURPLE + originDetails.get("origins:enderian"), null, null, cutStringIntoLines(originDescriptions.get("origins:enderian")));
        else enderian = itemProperties(enderian, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:shulk"))
            shulk = itemPropertiesMultipleLore(shulk, DARK_PURPLE + originDetails.get("origins:shulk"), null, null, cutStringIntoLines(originDescriptions.get("origins:shulk")));
        else shulk = itemProperties(shulk, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:arachnid"))
            arachnid = itemPropertiesMultipleLore(arachnid, RED + originDetails.get("origins:arachnid"), null, null, cutStringIntoLines(originDescriptions.get("origins:arachnid")));
        else arachnid = itemProperties(arachnid, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:creep"))
            creep = itemPropertiesMultipleLore(creep, GREEN + originDetails.get("origins:creep"), null, null, cutStringIntoLines(originDescriptions.get("origins:creep")));
        else creep = itemProperties(creep, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:phantom"))
            phantom = itemPropertiesMultipleLore(phantom, BLUE + originDetails.get("origins:phantom"), null, null, cutStringIntoLines(originDescriptions.get("origins:phantom")));
        else phantom = itemProperties(phantom, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:slimeling"))
            slimeling = itemPropertiesMultipleLore(slimeling, GREEN + originDetails.get("origins:slimeling"), null, null, cutStringIntoLines(originDescriptions.get("origins:slimeling")));
        else slimeling = itemProperties(slimeling, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:feline"))
            feline = itemPropertiesMultipleLore(feline, AQUA + originDetails.get("origins:feline"), null, null, cutStringIntoLines(originDescriptions.get("origins:feline")));
        else feline = itemProperties(feline, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:blazeborn"))
            blazeborn = itemPropertiesMultipleLore(blazeborn, GOLD + originDetails.get("origins:blazeborn"), null, null, cutStringIntoLines(originDescriptions.get("origins:blazeborn")));
        else blazeborn = itemProperties(blazeborn, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:starborne"))
            starborne = itemPropertiesMultipleLore(starborne, LIGHT_PURPLE + originDetails.get("origins:starborne"), null, null, cutStringIntoLines(originDescriptions.get("origins:starborne")));
        else starborne = itemProperties(starborne, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:merling"))
            merling = itemPropertiesMultipleLore(merling, BLUE + originDetails.get("origins:merling"), null, null, cutStringIntoLines(originDescriptions.get("origins:merling")));
        else merling = itemProperties(merling, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:allay"))
            allay = itemPropertiesMultipleLore(allay, AQUA + originDetails.get("origins:allay"), null, null, cutStringIntoLines(originDescriptions.get("origins:allay")));
        else allay = itemProperties(allay, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:rabbit"))
            rabbit = itemPropertiesMultipleLore(rabbit, GOLD + originDetails.get("origins:rabbit"), null, null, cutStringIntoLines(originDescriptions.get("origins:rabbit")));
        else rabbit = itemProperties(rabbit, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:bee"))
            bumblebee = itemPropertiesMultipleLore(bumblebee, YELLOW + originDetails.get("origins:bee"), null, null, cutStringIntoLines(originDescriptions.get("origins:bee")));
        else bumblebee = itemProperties(bumblebee, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:elytrian"))
            elytrian = itemPropertiesMultipleLore(elytrian, GRAY + originDetails.get("origins:elytrian"), null, null, cutStringIntoLines(originDescriptions.get("origins:elytrian")));
        else elytrian = itemProperties(elytrian, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:avian"))
            avian = itemPropertiesMultipleLore(avian, DARK_AQUA + originDetails.get("origins:avian"), null, null, cutStringIntoLines(originDescriptions.get("origins:avian")));
        else avian = itemProperties(avian, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:piglin"))
            piglin = itemPropertiesMultipleLore(piglin, GOLD + originDetails.get("origins:piglin"), null, null, cutStringIntoLines(originDescriptions.get("origins:piglin")));
        else piglin = itemProperties(piglin, RED + notFound, null, null, RED + notFound);
        if (originDetails.containsKey("origins:sculkling"))
            sculkling = itemPropertiesMultipleLore(sculkling, BLUE + originDetails.get("origins:sculkling"), null, null, cutStringIntoLines(originDescriptions.get("origins:sculkling")));
        else sculkling = itemProperties(sculkling, RED + notFound, null, null, RED + notFound);

        custom_originmenu = itemProperties(custom_originmenu, ChatColor.YELLOW + LangConfig.getLocalizedString(p, "menu.mainChoose.customMenu"), ItemFlag.HIDE_ENCHANTS, null, null);
        close = itemProperties(close, RED + LangConfig.getLocalizedString(p, "menu.mainChoose.close.name"), null, null, RED + LangConfig.getLocalizedString(p, "menu.mainChoose.close.description"));

        ItemStack randomOrb = itemProperties(orb.clone(), LIGHT_PURPLE + LangConfig.getLocalizedString(p, "menu.mainChoose.random"), null, null, null);
        NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "orb");
        ItemMeta randomOrbMeta = randomOrb.getItemMeta();
        randomOrbMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "orb");
        randomOrb.setItemMeta(randomOrbMeta);

        ItemStack[] mainMenuContents = {avian, arachnid, elytrian, shulk, feline, enderian, merling, blazeborn, phantom,
                background, background, background, background, human, background, background, background, background,
                filler, filler, filler, filler, filler, filler, filler, filler, filler,
                starborne, allay, rabbit, bumblebee, background, sculkling, creep, slimeling, piglin,
                background, background, background, background, background, background, background, background, background,
                filler, filler, filler, randomOrb, close, custom_originmenu, filler, filler, filler};

        return mainMenuContents;

    }

}
