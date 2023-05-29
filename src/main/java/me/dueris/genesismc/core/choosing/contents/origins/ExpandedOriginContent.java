package me.dueris.genesismc.core.choosing.contents.origins;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemProperties;
import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemPropertiesMultipleLore;
import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.*;

public class ExpandedOriginContent {

    public static @Nullable ItemStack @NotNull [] StarborneContents() {
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack star = new ItemStack(Material.NETHER_STAR);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack star_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info6 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info7 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info8 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info9 = new ItemStack(Material.FILLED_MAP);

        star_origin_info1 = itemProperties(star_origin_info1, UNDERLINE + "Wanderer of the Stars", null, null, WHITE + "You cannot sleep at night");
        star_origin_info2 = itemProperties(star_origin_info2, UNDERLINE + "Shooting Star", null, null, WHITE + "You can fling yourself into the air after a 5 second cooldown");
        star_origin_info3 = itemProperties(star_origin_info3, UNDERLINE + "Falling Stars", null, null, WHITE + "You can drop stars on your enemy every 30 seconds");
        star_origin_info4 = itemProperties(star_origin_info4, UNDERLINE + "Mysterious Power", null, null, WHITE + "When night falls, you have will be granted a special gift from the stars above");
        star_origin_info5 = itemProperties(star_origin_info5, UNDERLINE + "Supernova", null, null, WHITE + "When you die, you explode into a supernova");
        star_origin_info6 = itemProperties(star_origin_info6, UNDERLINE + "Cold Vacuum", null, null, WHITE + "You are used to the coldness of space, so you take double damage from fire");
        star_origin_info7 = itemProperties(star_origin_info7, UNDERLINE + "Stargazer", null, null, WHITE + "When exposed to the stars, you gain speed and regeneration, as a gift from the stars");
        star_origin_info8 = itemProperties(star_origin_info8, UNDERLINE + "Unknown Realms", null, null, WHITE + "Being in a realm without stars makes you weaker");
        star_origin_info9 = itemProperties(star_origin_info9, UNDERLINE + "Nonviolent", null, null, WHITE + "You have a chance to be immobilized upon taking damage, and your a vegetarian");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        star = itemProperties(star, "Starborne", null, Enchantment.ARROW_INFINITE, LIGHT_PURPLE + "Starborne Origin");
        ItemStack impact = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);

        impact = itemProperties(impact, WHITE + "Impact:" + YELLOW + " Medium", null, null, null);
        ItemStack[] stargui_items = {close, impact, impact, air, orb, air, impact, impact, close, air, air, air, air, star, air, air, air, air, air, air, star_origin_info1, star_origin_info2, star_origin_info3, star_origin_info4, star_origin_info5, air, air, air, air, star_origin_info6, star_origin_info7, star_origin_info8, star_origin_info9, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return stargui_items;
    }

    public static @Nullable ItemStack @NotNull [] AllayContents() {
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack allay = new ItemStack(Material.AMETHYST_SHARD);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack allay_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack allay_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack allay_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack allay_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack allay_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack allay_origin_info6 = new ItemStack(Material.FILLED_MAP);
        ItemStack allay_origin_info7 = new ItemStack(Material.FILLED_MAP);

        allay_origin_info1 = itemProperties(allay_origin_info1, UNDERLINE + "Little Fairy", null, null, WHITE + "You have small wings, you can fly and float");
        allay_origin_info2 = itemProperties(allay_origin_info2, UNDERLINE + "Blue Spirit", null, null, WHITE + "You are semi-translucent, half height, and glow in dark places. Also you're blue");
        allay_origin_info3 = itemProperties(allay_origin_info3, UNDERLINE + "Sounds of Music", null, null, WHITE + "You enjoy the sounds of music, and can use a jukebox as a respawn anchor");
        allay_origin_info4 = itemProperties(allay_origin_info4, UNDERLINE + "COOKIES", null, null, WHITE + "Cookies give the same saturation as steak");
        allay_origin_info5 = itemProperties(allay_origin_info5, UNDERLINE + "Treasure Finder", null, null, WHITE + "You have increased chances of getting treasure loot and villagers will lower their prices for you");
        allay_origin_info6 = itemProperties(allay_origin_info6, UNDERLINE + "Kinda Flamable", null, null, WHITE + "You burn easily, you take extra fire damage and have half health");
        allay_origin_info7 = itemProperties(allay_origin_info7, UNDERLINE + "Friendly Angel", null, null, WHITE + "You don't like to harm animals, you get nauseous when eating meat");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        allay = itemProperties(allay, "Allay", null, Enchantment.ARROW_INFINITE, AQUA + "Allay Origin");
        ItemStack impact = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        impact = itemProperties(impact, WHITE + "Impact:" + GREEN + " Low", null, null, null);
        ItemStack[] allaygui_items = {close, impact, air, air, orb, air, air, impact, close, air, air, air, air, allay, air, air, air, air, air, air, allay_origin_info1, allay_origin_info2, allay_origin_info3, allay_origin_info4, allay_origin_info5, air, air, air, air, allay_origin_info6, allay_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return allaygui_items;
    }

    public static @Nullable ItemStack @NotNull [] RabbitContents() {
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack rabbit = new ItemStack(Material.CARROT);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack rabbit_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack rabbit_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack rabbit_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack rabbit_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack rabbit_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack rabbit_origin_info6 = new ItemStack(Material.FILLED_MAP);

        rabbit_origin_info1 = itemPropertiesMultipleLore(rabbit_origin_info1, UNDERLINE + "Leap", null, null, Arrays.asList(WHITE + "You leap in the direction you're looking to", WHITE + "You can use /toggle to toggle this ability"));
        rabbit_origin_info2 = itemProperties(rabbit_origin_info2, UNDERLINE + "Strong Hopper", null, null, WHITE + "You jump significantly higher");
        rabbit_origin_info3 = itemProperties(rabbit_origin_info3, UNDERLINE + "Shock Absorption", null, null, WHITE + "You take less fall damage");
        rabbit_origin_info4 = itemProperties(rabbit_origin_info4, UNDERLINE + "Delicious", null, null, WHITE + "You may drop a rabbit's foot when hit");
        rabbit_origin_info5 = itemProperties(rabbit_origin_info5, UNDERLINE + "Picky Eater", null, null, WHITE + "You can only eat carrots and golden carrots");
        rabbit_origin_info6 = itemProperties(rabbit_origin_info6, UNDERLINE + "Fragile", null, null, WHITE + "You have 3 less hearts");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        rabbit = itemProperties(rabbit, "Rabbit", null, Enchantment.ARROW_INFINITE, GOLD + "Bunny Origin");
        ItemStack impact = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        impact = itemProperties(impact, WHITE + "Impact:" + GREEN + " Low", null, null, null);
        ItemStack[] rabbitgui_items = {close, impact, air, air, orb, air, air, impact, close, air, air, air, air, rabbit, air, air, air, air, air, air, rabbit_origin_info1, rabbit_origin_info2, rabbit_origin_info3, rabbit_origin_info4, rabbit_origin_info5, air, air, air, air, rabbit_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return rabbitgui_items;
    }

    public static @Nullable ItemStack @NotNull [] BeeContents() {
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack bee = new ItemStack(Material.HONEYCOMB);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack bee_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack bee_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack bee_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack bee_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack bee_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack bee_origin_info6 = new ItemStack(Material.FILLED_MAP);
        ItemStack bee_origin_info7 = new ItemStack(Material.FILLED_MAP);

        bee_origin_info1 = itemProperties(bee_origin_info1, UNDERLINE + "Featherweight", null, null, WHITE + "You fall as gently to the ground as a feather would unless you shift");
        bee_origin_info2 = itemProperties(bee_origin_info2, UNDERLINE + "Poisonous", null, null, WHITE + "Hitting someone gives them poison for 2 seconds");
        bee_origin_info3 = itemProperties(bee_origin_info3, UNDERLINE + "Bloom", null, null, WHITE + "You gain regeneration when near flowers");
        bee_origin_info4 = itemProperties(bee_origin_info4, UNDERLINE + "Flight", null, null, WHITE + "You can fly, just like a bee!(WHATT)");
        bee_origin_info5 = itemProperties(bee_origin_info5, UNDERLINE + "Nighttime", null, null, WHITE + "You are sleepy at night, so you walk and fly slower");
        bee_origin_info6 = itemProperties(bee_origin_info6, UNDERLINE + "Lifespan", null, null, WHITE + "You have 3 less hearts");
        bee_origin_info7 = itemProperties(bee_origin_info7, UNDERLINE + "Rain", null, null, WHITE + "You cannot fly when in the rain and are weaker while wet");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        bee = itemProperties(bee, "Bumblebee", null, Enchantment.ARROW_INFINITE, YELLOW + "Bee Origin");
        ItemStack impact = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        impact = itemProperties(impact, WHITE + "Impact:" + GREEN + " Low", null, null, null);
        ItemStack[] beegui_items = {close, impact, air, air, orb, air, air, impact, close, air, air, air, air, bee, air, air, air, air, air, air, bee_origin_info1, bee_origin_info2, bee_origin_info3, bee_origin_info4, bee_origin_info5, air, air, air, air, bee_origin_info6, bee_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return beegui_items;
    }

    public static @Nullable ItemStack @NotNull [] PiglinContents() {
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack piglin = new ItemStack(Material.GOLD_INGOT);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack piglin_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack piglin_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack piglin_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack piglin_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack piglin_origin_info5 = new ItemStack(Material.FILLED_MAP);

        piglin_origin_info1 = itemProperties(piglin_origin_info1, UNDERLINE + "I like to be SHINY", null, null, WHITE + "Golden tools deal extra damage and gold armour has more protection");
        piglin_origin_info2 = itemProperties(piglin_origin_info2, UNDERLINE + "Friendly Frenemies", null, null, WHITE + "Piglins won't attack you unless provoked, Brutes will still attack on sight");
        piglin_origin_info3 = itemProperties(piglin_origin_info3, UNDERLINE + "Nether Dweller", null, null, WHITE + "Your natural spawn is in the Nether and you can only eat meat");
        piglin_origin_info4 = itemProperties(piglin_origin_info4, UNDERLINE + "Colder Realms", null, null, WHITE + "When outside of the Nether, you zombify and become immune to fire and slower");
        piglin_origin_info5 = itemProperties(piglin_origin_info5, UNDERLINE + "BLUE FIRE SPOOKY", null, null, WHITE + "You are afraid of soul fire, becoming weak when near it");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        piglin = itemProperties(piglin, "Piglin", null, Enchantment.ARROW_INFINITE, GOLD + "Piglin Origin");
        ItemStack impact = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);

        impact = itemProperties(impact, WHITE + "Impact:" + YELLOW + " Medium", null, null, null);
        ItemStack[] piglingui_items = {close, impact, impact, air, orb, air, impact, impact, close, air, air, air, air, piglin, air, air, air, air, air, air, piglin_origin_info1, piglin_origin_info2, piglin_origin_info3, piglin_origin_info4, piglin_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return piglingui_items;
    }

    public static @Nullable ItemStack @NotNull [] SculkContents() {
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack sculk = new ItemStack(Material.ECHO_SHARD);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack sculk_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info6 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info7 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info8 = new ItemStack(Material.FILLED_MAP);

        sculk_origin_info1 = itemProperties(sculk_origin_info1, UNDERLINE + "One of Them", null, null, WHITE + "You spawn in the Deep Dark");
        sculk_origin_info2 = itemProperties(sculk_origin_info2, UNDERLINE + "Amongst Your People", null, null, WHITE + "You get buffs while near sculk blocks");
        sculk_origin_info3 = itemProperties(sculk_origin_info3, UNDERLINE + "Best Friends Forever", null, null, WHITE + "The Warden wont attack you, and you don't trigger Sculk Shriekers");
        sculk_origin_info4 = itemProperties(sculk_origin_info4, UNDERLINE + "Afraid of the Light", null, null, WHITE + "You are weaker while in sunlight");
        sculk_origin_info5 = itemProperties(sculk_origin_info5, UNDERLINE + "It Grows", null, null, WHITE + "Upon dying, a small patch of sculk will grow around you, you gain some saturation");
        sculk_origin_info6 = itemProperties(sculk_origin_info6, UNDERLINE + "Echo Pulse", null, null, WHITE + "You can see all entities around you, you gain some saturation");
        sculk_origin_info7 = itemProperties(sculk_origin_info7, UNDERLINE + "Decaying Essence", null, null, WHITE + "All armour you wear will slowly deteriorate");
        sculk_origin_info8 = itemProperties(sculk_origin_info8, UNDERLINE + "Carrier of Echos", null, null, WHITE + "You emmit a sonic boom upon Shift-Clicking your Boom keybind, or your Boom item(30 second cooldown)");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        sculk = itemProperties(sculk, "Sculkling", null, Enchantment.ARROW_INFINITE, BLUE + "Sculk Origin");
        ItemStack impact = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);

        impact = itemProperties(impact, WHITE + "Impact:" + YELLOW + " Medium", null, null, null);
        ItemStack[] sculkgui_items = {close, impact, impact, air, orb, air, impact, impact, close, air, air, air, air, sculk, air, air, air, air, air, air, sculk_origin_info1, sculk_origin_info2, sculk_origin_info3, sculk_origin_info4, sculk_origin_info5, air, air, air, air, sculk_origin_info6, sculk_origin_info7, sculk_origin_info8, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return sculkgui_items;
    }

    public static @Nullable ItemStack @NotNull [] CreepContents() {
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack creep = new ItemStack(Material.GUNPOWDER);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack creep_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack creep_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack creep_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack creep_origin_info4 = new ItemStack(Material.FILLED_MAP);

        creep_origin_info1 = itemPropertiesMultipleLore(creep_origin_info1, UNDERLINE + "BOOOOOM", null, null, Arrays.asList(WHITE + "You can explode at will,", WHITE + "but you take 5 hearts of damage"));
        creep_origin_info2 = itemProperties(creep_origin_info2, UNDERLINE + "Charged", null, null, WHITE + "During thunderstorms, you are significantly stronger");
        creep_origin_info3 = itemProperties(creep_origin_info3, UNDERLINE + "You got a Friend in Me", null, null, WHITE + "Other creepers will not attack you");
        creep_origin_info4 = itemPropertiesMultipleLore(creep_origin_info4, UNDERLINE + "Felinephobia", null, null, Arrays.asList(WHITE + "You are scared of cats and you", WHITE + "will take damage when you are close"));

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        creep = itemProperties(creep, "Creep", null, Enchantment.ARROW_INFINITE, GREEN + "Creeper Origin");
        ItemStack impact = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        impact = itemProperties(impact, WHITE + "Impact:" + GREEN + " Low", null, null, null);
        ItemStack[] creepgui_items = {close, impact, air, air, orb, air, air, impact, close, air, air, air, air, creep, air, air, air, air, air, air, creep_origin_info1, creep_origin_info2, creep_origin_info3, creep_origin_info4, blank, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return creepgui_items;
    }

    public static @Nullable ItemStack @NotNull [] SlimelingContents() {
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack slime = new ItemStack(Material.SLIME_BALL);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack slime_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack slime_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack slime_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack slime_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack slime_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack slime_origin_info6 = new ItemStack(Material.FILLED_MAP);

        slime_origin_info1 = itemProperties(slime_origin_info1, UNDERLINE + "Bouncy", null, null, WHITE + "You bounce on any block as if it were a slime block");
        slime_origin_info2 = itemProperties(slime_origin_info2, UNDERLINE + "Not Very Solid", null, null, WHITE + "Upon being hit, you have a chance to split and create small slimes");
        slime_origin_info3 = itemProperties(slime_origin_info3, UNDERLINE + "Improved Jump", null, null, WHITE + "You have an improved leap at the cost of movement speed");
        slime_origin_info4 = itemProperties(slime_origin_info4, UNDERLINE + "Great Leap", null, null, WHITE + "Upon shifting for 4 seconds(nothing in hand), you leap in the direction you are looking");
        slime_origin_info5 = itemProperties(slime_origin_info5, UNDERLINE + "Slimy Skin", null, null, WHITE + "You have the green translucent skin of a slime");
        slime_origin_info6 = itemProperties(slime_origin_info6, UNDERLINE + "Burnable", null, null, WHITE + "You burn when in hotter biomes");

        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel Choosing");
        menu = itemProperties(menu, ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
        slime = itemProperties(slime, "Slimeling", null, Enchantment.ARROW_INFINITE, GREEN + "Slime Origin");
        ItemStack impact = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        impact = itemProperties(impact, WHITE + "Impact:" + GREEN + " Low", null, null, null);
        ItemStack[] slimegui_items = {close, impact, air, air, orb, air, air, impact, close, air, air, air, air, slime, air, air, air, air, air, air, slime_origin_info1, slime_origin_info2, slime_origin_info3, slime_origin_info4, slime_origin_info5, air, air, air, air, slime_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return slimegui_items;
    }
}
