package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.md_5.bungee.api.chat.Keybinds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.*;

public class Info extends SubCommand implements Listener {
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "info library for info on origins";
    }

    @Override
    public String getSyntax() {
        return "/origin info <get> <args>";
    }

    @Override
    public void perform(Player p, String[] args) {
        @Nullable String origintag = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if(args[1] != null && args[1].equalsIgnoreCase("get")){
            if(args.length >= 2){
                if(args[2].equalsIgnoreCase("origin")){
                    if(origintag.equalsIgnoreCase("genesis:origin-enderian")){
                        p.sendMessage(p.getName() + " has the Enderian origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-shulk")){
                        p.sendMessage(p.getName() + " has the Shulk origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-human") || origintag.equalsIgnoreCase("genesis:origin-null")){
                        p.sendMessage(p.getName() + " has the Human origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-arachnid")){
                        p.sendMessage(p.getName() + " has the Arachnid origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-creep")){
                        p.sendMessage(p.getName() + " has the Creep origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-phantom")){
                        p.sendMessage(p.getName() + " has the Phantom origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-slimeling")){
                        p.sendMessage(p.getName() + " has the Slimeling origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-vexian")){
                        p.sendMessage(p.getName() + " has the Vexian origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-blazeborn")){
                        p.sendMessage(p.getName() + " has the Blazeborn origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-starborn")){
                        p.sendMessage(p.getName() + " has the Starborne origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-merling")){
                        p.sendMessage(p.getName() + " has the Merling origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-allay")){
                        p.sendMessage(p.getName() + " has the Allay origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-rabbit")){
                        p.sendMessage(p.getName() + " has the Rabbit origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-bee")){
                        p.sendMessage(p.getName() + " has the Bumblebee origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-elytrian")){
                        p.sendMessage(p.getName() + " has the Elytrian origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-avian")){
                        p.sendMessage(p.getName() + " has the Avian origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-piglin")){
                        p.sendMessage(p.getName() + " has the Piglin origin");
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-sculkling")){
                        p.sendMessage(p.getName() + " has the Sculking origin");
                    }
                }
                if(args[2].equalsIgnoreCase("keybind")){
                    p.sendMessage("[GenesisMC] Unable to get keybind at this time. Please use your SWAP_HANDS key. Default: F");
                }
                if(args[2].equalsIgnoreCase("help")){
                    if(origintag.equalsIgnoreCase("genesis:origin-enderian")){
                        Inventory endergui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");

                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack ender = new ItemStack(Material.ENDER_PEARL);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack ender_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack blank = new ItemStack(Material.PAPER);

                        ItemStack ender_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack ender_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack ender_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack ender_origin_info5 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta ender_origin_info1_meta = ender_origin_info1.getItemMeta();
                        ender_origin_info1_meta.setDisplayName(UNDERLINE + "Teleportaion");
                        ender_origin_info1_meta.setLore(Arrays.asList(WHITE + "You have an infinite ender pearl", WHITE + "that deals no damage"));
                        ender_origin_info1.setItemMeta(ender_origin_info1_meta);

                        ItemMeta ender_origin_info2_meta = ender_origin_info2.getItemMeta();
                        ender_origin_info2_meta.setDisplayName(UNDERLINE + "Hydrophobia");
                        ender_origin_info2_meta.setLore(Arrays.asList(WHITE + "You take damage while in", WHITE + "contact with water"));
                        ender_origin_info2.setItemMeta(ender_origin_info2_meta);

                        ItemMeta ender_origin_info3_meta = ender_origin_info3.getItemMeta();
                        ender_origin_info3_meta.setDisplayName(UNDERLINE + "Delicate touch");
                        ArrayList<String> ender_origin_info3_lore = new ArrayList<>();
                        ender_origin_info3_lore.add(WHITE + "You have silk touch hands");
                        ender_origin_info3_meta.setLore(ender_origin_info3_lore);
                        ender_origin_info3.setItemMeta(ender_origin_info3_meta);

                        ItemMeta ender_origin_info4_meta = ender_origin_info4.getItemMeta();
                        ender_origin_info4_meta.setDisplayName(UNDERLINE + "Brethren of the End");
                        ender_origin_info4_meta.setLore(Arrays.asList(WHITE + "Enderman don't get mad at you", WHITE + "upon looking at them"));
                        ender_origin_info4.setItemMeta(ender_origin_info4_meta);

                        ItemMeta ender_origin_info5_meta = ender_origin_info5.getItemMeta();
                        ender_origin_info5_meta.setDisplayName(UNDERLINE + "Bearer of Pearls");
                        ArrayList<String> ender_origin_info5_lore = new ArrayList<>();
                        ender_origin_info5_lore.add(WHITE + "You always drop 0-2 ender pearls upon death");
                        ender_origin_info5_meta.setLore(ender_origin_info5_lore);
                        ender_origin_info5.setItemMeta(ender_origin_info5_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta ender_meta = ender.getItemMeta();
                        ender_meta.setDisplayName("Enderian");
                        ArrayList<String> ender_lore = new ArrayList<>();
                        ender_lore.add(LIGHT_PURPLE + "Enderman Origin");
                        ender_meta.setLore(ender_lore);
                        ender.setItemMeta(ender_meta);


                        ItemStack[] endergui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, ender, air, air, air, air, air, air, ender_origin_info1, ender_origin_info2, ender_origin_info3, ender_origin_info4, ender_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        endergui.setContents(endergui_items);
                        p.openInventory(endergui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-shulk")){
                        Inventory shulkgui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");

                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack shulk = new ItemStack(Material.SHULKER_SHELL);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack shulk_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack shulk_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack shulk_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack shulk_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack shulk_origin_info5 = new ItemStack(Material.FILLED_MAP);

                        ItemMeta shulk_origin_info1_meta = shulk_origin_info1.getItemMeta();
                        //Component.keybind("key.drop")
                        shulk_origin_info1_meta.setDisplayName(UNDERLINE + "Hoarder" + DARK_GRAY + " (/shulk open, or press the F key to open)");
                        shulk_origin_info1_meta.setLore(Arrays.asList(WHITE + "You have 9 extra inventory slots.", WHITE + "You keep these items upon death"));
                        shulk_origin_info1.setItemMeta(shulk_origin_info1_meta);

                        ItemMeta shulk_origin_info2_meta = shulk_origin_info2.getItemMeta();
                        shulk_origin_info2_meta.setDisplayName(UNDERLINE + "Sturdy Skin");
                        ArrayList<String> shulk_origin_info2_lore = new ArrayList<>();
                        shulk_origin_info2_lore.add(WHITE + "Your skin has natural protection");
                        shulk_origin_info2_meta.setLore(shulk_origin_info2_lore);
                        shulk_origin_info2.setItemMeta(shulk_origin_info2_meta);

                        ItemMeta shulk_origin_info3_meta = shulk_origin_info3.getItemMeta();
                        shulk_origin_info3_meta.setDisplayName(UNDERLINE + "Strong Arms");
                        ArrayList<String> shulk_origin_info3_lore = new ArrayList<>();
                        shulk_origin_info3_lore.add(WHITE + "You can break natural stones without a pickaxe");
                        shulk_origin_info3_meta.setLore(shulk_origin_info3_lore);
                        shulk_origin_info3.setItemMeta(shulk_origin_info3_meta);

                        ItemMeta shulk_origin_info4_meta = shulk_origin_info4.getItemMeta();
                        shulk_origin_info4_meta.setDisplayName(UNDERLINE + "Unwieldy");
                        ArrayList<String> shulk_origin_info4_lore = new ArrayList<>();
                        shulk_origin_info4_lore.add(WHITE + "You cannot hold a shield");
                        shulk_origin_info4_meta.setLore(shulk_origin_info4_lore);
                        shulk_origin_info4.setItemMeta(shulk_origin_info4_meta);

                        ItemMeta shulk_origin_info5_meta = shulk_origin_info5.getItemMeta();
                        shulk_origin_info5_meta.setDisplayName(UNDERLINE + "Large Appetite");
                        ArrayList<String> shulk_origin_info5_lore = new ArrayList<>();
                        shulk_origin_info5_lore.add(WHITE + "You exhaust much quicker than others");
                        shulk_origin_info5_meta.setLore(shulk_origin_info5_lore);
                        shulk_origin_info5.setItemMeta(shulk_origin_info5_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta shulk_meta = shulk.getItemMeta();
                        shulk_meta.setDisplayName("Shulk");
                        ArrayList<String> shulk_lore = new ArrayList<>();
                        shulk_lore.add(LIGHT_PURPLE + "Shulker Origin");
                        shulk_meta.setLore(shulk_lore);
                        shulk.setItemMeta(shulk_meta);


                        ItemStack[] shulkgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, shulk, air, air, air, air, air, air, shulk_origin_info1, shulk_origin_info2, shulk_origin_info3, shulk_origin_info4, shulk_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        shulkgui.setContents(shulkgui_items);
                        p.openInventory(shulkgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-human") || origintag.equalsIgnoreCase("genesis:origin-null")){
                        Inventory humangui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);

                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack human = new ItemStack(Material.PLAYER_HEAD);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack human_origin_info = new ItemStack(Material.FILLED_MAP);
                        ItemStack blank = new ItemStack(Material.PAPER);


                        ItemMeta human_origin_info_meta = human_origin_info.getItemMeta();
                        human_origin_info_meta.setDisplayName("Nothing");
                        ArrayList<String> human_origin_info_lore = new ArrayList<>();
                        human_origin_info_lore.add(WHITE + "Enough said.");
                        human_origin_info_meta.setLore(human_origin_info_lore);
                        human_origin_info.setItemMeta(human_origin_info_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta human_meta = human.getItemMeta();
                        human_meta.setDisplayName("Human");
                        ArrayList<String> human_lore = new ArrayList<>();
                        human_lore.add(WHITE + "Human Origin");
                        human_meta.setLore(human_lore);
                        human.setItemMeta(human_meta);


                        ItemStack[] humangui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, human, air, air, air, air, air, air, blank, blank, human_origin_info, blank, blank, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        humangui.setContents(humangui_items);
                        p.openInventory(humangui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-arachnid")){
                        Inventory spidergui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack spider = new ItemStack(Material.COBWEB);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);

                        ItemStack spider_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack spider_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack spider_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack spider_origin_info4 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta spider_origin_info1_meta = spider_origin_info1.getItemMeta();
                        spider_origin_info1_meta.setDisplayName(UNDERLINE + "SpiderMan");
                        ArrayList<String> spider_origin_info1_lore = new ArrayList<>();
                        spider_origin_info1_lore.add(WHITE + "You can climb up walls, but not when in the rain");
                        spider_origin_info1_meta.setLore(spider_origin_info1_lore);
                        spider_origin_info1.setItemMeta(spider_origin_info1_meta);

                        ItemMeta spider_origin_info2_meta = spider_origin_info2.getItemMeta();
                        spider_origin_info2_meta.setDisplayName(UNDERLINE + "Weaver");
                        spider_origin_info2_meta.setLore(Arrays.asList(WHITE + "You hinder your foes with", WHITE + "cobwebs upon attacking them"));
                        spider_origin_info2.setItemMeta(spider_origin_info2_meta);

                        ItemMeta spider_origin_info3_meta = spider_origin_info3.getItemMeta();
                        spider_origin_info3_meta.setDisplayName(UNDERLINE + "Squishable");
                        ArrayList<String> spider_origin_info3_lore = new ArrayList<>();
                        spider_origin_info3_lore.add(WHITE + "You have 3 less hearts");
                        spider_origin_info3_meta.setLore(spider_origin_info3_lore);
                        spider_origin_info3.setItemMeta(spider_origin_info3_meta);

                        ItemMeta spider_origin_info4_meta = spider_origin_info4.getItemMeta();
                        spider_origin_info4_meta.setDisplayName(UNDERLINE + "Tiny Carnivore");
                        ArrayList<String> spider_origin_info4_lore = new ArrayList<>();
                        spider_origin_info4_lore.add(WHITE + "You can only eat meat");
                        spider_origin_info4_meta.setLore(spider_origin_info4_lore);
                        spider_origin_info4.setItemMeta(spider_origin_info4_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta spider_meta = spider.getItemMeta();
                        spider_meta.setDisplayName("Arachnid");
                        ArrayList<String> spider_lore = new ArrayList<>();
                        spider_lore.add(RED + "Spider Origin");
                        spider_meta.setLore(spider_lore);
                        spider.setItemMeta(spider_meta);


                        ItemStack[] spidergui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, spider, air, air, air, air, air, air, spider_origin_info1, spider_origin_info2, spider_origin_info3, spider_origin_info4, blank, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        spidergui.setContents(spidergui_items);
                        p.openInventory(spidergui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-creep")){
                        Inventory creepgui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack creep = new ItemStack(Material.GUNPOWDER);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);

                        ItemStack creep_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack creep_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack creep_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack creep_origin_info4 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta creep_origin_info1_meta = creep_origin_info1.getItemMeta();
                        creep_origin_info1_meta.setDisplayName(UNDERLINE + "BOOOOOM");
                        creep_origin_info1_meta.setLore(Arrays.asList(WHITE + "You can explode at will,", WHITE + "but you take 5 hearts of damage"));
                        creep_origin_info1.setItemMeta(creep_origin_info1_meta);

                        ItemMeta creep_origin_info2_meta = creep_origin_info2.getItemMeta();
                        creep_origin_info2_meta.setDisplayName(UNDERLINE + "Charged");
                        ArrayList<String> creep_origin_info2_lore = new ArrayList<>();
                        creep_origin_info2_lore.add(WHITE + "During thunderstorms, you are significantly stronger");
                        creep_origin_info2_meta.setLore(creep_origin_info2_lore);
                        creep_origin_info2.setItemMeta(creep_origin_info2_meta);

                        ItemMeta creep_origin_info3_meta = creep_origin_info3.getItemMeta();
                        creep_origin_info3_meta.setDisplayName(UNDERLINE + "You got a Friend in Me");
                        ArrayList<String> creep_origin_info3_lore = new ArrayList<>();
                        creep_origin_info3_lore.add(WHITE + "Other creepers will not attack you");
                        creep_origin_info3_meta.setLore(creep_origin_info3_lore);
                        creep_origin_info3.setItemMeta(creep_origin_info3_meta);

                        ItemMeta creep_origin_info4_meta = creep_origin_info4.getItemMeta();
                        creep_origin_info4_meta.setDisplayName(UNDERLINE + "Felinephobia");
                        creep_origin_info4_meta.setLore(Arrays.asList(WHITE + "You are scared of cats and you", WHITE + "will take damage when you are close"));
                        creep_origin_info4.setItemMeta(creep_origin_info4_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta creep_meta = creep.getItemMeta();
                        creep_meta.setDisplayName("Creep");
                        ArrayList<String> creep_lore = new ArrayList<>();
                        creep_lore.add(GREEN + "Creeper Origin");
                        creep_meta.setLore(creep_lore);
                        creep.setItemMeta(creep_meta);


                        ItemStack[] creepgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, creep, air, air, air, air, air, air, creep_origin_info1, creep_origin_info2, creep_origin_info3, creep_origin_info4, blank, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        creepgui.setContents(creepgui_items);
                        p.openInventory(creepgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-phantom")){
                        Inventory phantomgui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");

                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack phantom = new ItemStack(Material.PHANTOM_MEMBRANE);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack phantom_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack phantom_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack phantom_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack phantom_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack phantom_origin_info5 = new ItemStack(Material.FILLED_MAP);
                        ItemStack phantom_origin_info6 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta phantom_origin_info1_meta = phantom_origin_info1.getItemMeta();
                        phantom_origin_info1_meta.setDisplayName(UNDERLINE + "Translucent");
                        ArrayList<String> phantom_origin_info1_lore = new ArrayList<>();
                        phantom_origin_info1_lore.add(WHITE + "You are slightly translucent, and at night you become more solid");
                        phantom_origin_info1_meta.setLore(phantom_origin_info1_lore);
                        phantom_origin_info1.setItemMeta(phantom_origin_info1_meta);

                        ItemMeta phantom_origin_info2_meta = phantom_origin_info2.getItemMeta();
                        phantom_origin_info2_meta.setDisplayName(UNDERLINE + "Not Really a Vampire");
                        ArrayList<String> phantom_origin_info2_lore = new ArrayList<>();
                        phantom_origin_info2_lore.add(WHITE + "You take damage from sunlight");
                        phantom_origin_info2_meta.setLore(phantom_origin_info2_lore);
                        phantom_origin_info2.setItemMeta(phantom_origin_info2_meta);

                        ItemMeta phantom_origin_info3_meta = phantom_origin_info3.getItemMeta();
                        phantom_origin_info3_meta.setDisplayName(UNDERLINE + "Phasing" + DARK_GRAY + "Press F to activate, or use item");
                        ArrayList<String> phantom_origin_info3_lore = new ArrayList<>();
                        phantom_origin_info3_lore.add(WHITE + "You can turn into your \"Phantom Form\", allowing you to walk through walls");
                        phantom_origin_info3_meta.setLore(phantom_origin_info3_lore);
                        phantom_origin_info3.setItemMeta(phantom_origin_info3_meta);

                        ItemMeta phantom_origin_info4_meta = phantom_origin_info4.getItemMeta();
                        phantom_origin_info4_meta.setDisplayName(UNDERLINE + "Fast Metabolism");
                        ArrayList<String> phantom_origin_info4_lore = new ArrayList<>();
                        phantom_origin_info4_lore.add(WHITE + "While in Phantom Form, you loose twice as much hunger");
                        phantom_origin_info4_meta.setLore(phantom_origin_info4_lore);
                        phantom_origin_info4.setItemMeta(phantom_origin_info4_meta);

                        ItemMeta phantom_origin_info5_meta = phantom_origin_info5.getItemMeta();
                        phantom_origin_info5_meta.setDisplayName(UNDERLINE + "Fragile Creature");
                        ArrayList<String> phantom_origin_info5_lore = new ArrayList<>();
                        phantom_origin_info5_lore.add(WHITE + "You have 3 less hearts");
                        phantom_origin_info5_meta.setLore(phantom_origin_info5_lore);
                        phantom_origin_info5.setItemMeta(phantom_origin_info5_meta);

                        ItemMeta phantom_origin_info6_meta = phantom_origin_info6.getItemMeta();
                        phantom_origin_info6_meta.setDisplayName(UNDERLINE + "Invisibility");
                        ArrayList<String> phantom_origin_info6_lore = new ArrayList<>();
                        phantom_origin_info6_lore.add(WHITE + "While phantomized, you become fully invisible.");
                        phantom_origin_info6_meta.setLore(phantom_origin_info6_lore);
                        phantom_origin_info6.setItemMeta(phantom_origin_info6_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta phantom_meta = phantom.getItemMeta();
                        phantom_meta.setDisplayName("Phantom");
                        ArrayList<String> phantom_lore = new ArrayList<>();
                        phantom_lore.add(BLUE + "Phantom Origin");
                        phantom_meta.setLore(phantom_lore);
                        phantom.setItemMeta(phantom_meta);


                        ItemStack[] phantomgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, phantom, air, air, air, air, air, air, phantom_origin_info1, phantom_origin_info2, phantom_origin_info3, phantom_origin_info4, phantom_origin_info5, air, air, air, air, phantom_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        phantomgui.setContents(phantomgui_items);
                        p.openInventory(phantomgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-slimeling")){
                        Inventory slimegui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");

                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack slime = new ItemStack(Material.SLIME_BALL);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack slime_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack slime_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack slime_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack slime_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack slime_origin_info5 = new ItemStack(Material.FILLED_MAP);
                        ItemStack slime_origin_info6 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta slime_origin_info1_meta = slime_origin_info1.getItemMeta();
                        slime_origin_info1_meta.setDisplayName(UNDERLINE + "Bouncy");
                        ArrayList<String> slime_origin_info1_lore = new ArrayList<>();
                        slime_origin_info1_lore.add(WHITE + "You bounce on any block as if it were a slime block");
                        slime_origin_info1_meta.setLore(slime_origin_info1_lore);
                        slime_origin_info1.setItemMeta(slime_origin_info1_meta);

                        ItemMeta slime_origin_info2_meta = slime_origin_info2.getItemMeta();
                        slime_origin_info2_meta.setDisplayName(UNDERLINE + "Not Very Solid");
                        ArrayList<String> slime_origin_info2_lore = new ArrayList<>();
                        slime_origin_info2_lore.add(WHITE + "Upon being hit, you have a chance to split and create small slimes");
                        slime_origin_info2_meta.setLore(slime_origin_info2_lore);
                        slime_origin_info2.setItemMeta(slime_origin_info2_meta);

                        ItemMeta slime_origin_info3_meta = slime_origin_info3.getItemMeta();
                        slime_origin_info3_meta.setDisplayName(UNDERLINE + "Improved Jump");
                        ArrayList<String> slime_origin_info3_lore = new ArrayList<>();
                        slime_origin_info3_lore.add(WHITE + "You have an improved leap at the cost of movement speed");
                        slime_origin_info3_meta.setLore(slime_origin_info3_lore);
                        slime_origin_info3.setItemMeta(slime_origin_info3_meta);

                        ItemMeta slime_origin_info4_meta = slime_origin_info4.getItemMeta();
                        slime_origin_info4_meta.setDisplayName(UNDERLINE + "Great Leap");
                        ArrayList<String> slime_origin_info4_lore = new ArrayList<>();
                        slime_origin_info4_lore.add(WHITE + "Upon shifting for 4 seconds(nothing in hand), you leap in the direction you are looking");
                        slime_origin_info4_meta.setLore(slime_origin_info4_lore);
                        slime_origin_info4.setItemMeta(slime_origin_info4_meta);

                        ItemMeta slime_origin_info5_meta = slime_origin_info5.getItemMeta();
                        slime_origin_info5_meta.setDisplayName(UNDERLINE + "Slimy Skin");
                        ArrayList<String> slime_origin_info5_lore = new ArrayList<>();
                        slime_origin_info5_lore.add(WHITE + "You have the green translucent skin of a slime");
                        slime_origin_info5_meta.setLore(slime_origin_info5_lore);
                        slime_origin_info5.setItemMeta(slime_origin_info5_meta);

                        ItemMeta slime_origin_info6_meta = slime_origin_info6.getItemMeta();
                        slime_origin_info6_meta.setDisplayName(UNDERLINE + "Burnable");
                        ArrayList<String> slime_origin_info6_lore = new ArrayList<>();
                        slime_origin_info6_lore.add(WHITE + "You burn when in hotter biomes");
                        slime_origin_info6_meta.setLore(slime_origin_info6_lore);
                        slime_origin_info6.setItemMeta(slime_origin_info6_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta slime_meta = slime.getItemMeta();
                        slime_meta.setDisplayName("Slimeling");
                        ArrayList<String> slime_lore = new ArrayList<>();
                        slime_lore.add(GREEN + "Slime Origin");
                        slime_meta.setLore(slime_lore);
                        slime.setItemMeta(slime_meta);


                        ItemStack[] slimegui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, slime, air, air, air, air, air, air, slime_origin_info1, slime_origin_info2, slime_origin_info3, slime_origin_info4, slime_origin_info5, air, air, air, air, slime_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        slimegui.setContents(slimegui_items);
                        p.openInventory(slimegui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-vexian")){
                        Inventory vexgui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack vex = new ItemStack(Material.IRON_SWORD);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);

                        ItemStack vex_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack vex_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack vex_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack vex_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack vex_origin_info5 = new ItemStack(Material.FILLED_MAP);
                        ItemStack vex_origin_info6 = new ItemStack(Material.FILLED_MAP);
                        ItemStack vex_origin_info7 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta vex_origin_info1_meta = vex_origin_info1.getItemMeta();
                        vex_origin_info1_meta.setDisplayName(UNDERLINE + "Weightless Soul");
                        ArrayList<String> vex_origin_info1_lore = new ArrayList<>();
                        vex_origin_info1_lore.add(WHITE + "You can fly. Really fast..");
                        vex_origin_info1_meta.setLore(vex_origin_info1_lore);
                        vex_origin_info1.setItemMeta(vex_origin_info1_meta);

                        ItemMeta vex_origin_info2_meta = vex_origin_info2.getItemMeta();
                        vex_origin_info2_meta.setDisplayName(UNDERLINE + "Raging Vex");
                        ArrayList<String> vex_origin_info2_lore = new ArrayList<>();
                        vex_origin_info2_lore.add(WHITE + "You gain strength 2 and speed 3 upon being hit for 2 seconds");
                        vex_origin_info2_meta.setLore(vex_origin_info2_lore);
                        vex_origin_info2.setItemMeta(vex_origin_info2_meta);

                        ItemMeta vex_origin_info3_meta = vex_origin_info3.getItemMeta();
                        vex_origin_info3_meta.setDisplayName(UNDERLINE + "Matterless");
                        ArrayList<String> vex_origin_info3_lore = new ArrayList<>();
                        vex_origin_info3_lore.add(WHITE + "You can fly through solid blocks");
                        vex_origin_info3_meta.setLore(vex_origin_info3_lore);
                        vex_origin_info3.setItemMeta(vex_origin_info3_meta);

                        ItemMeta vex_origin_info4_meta = vex_origin_info4.getItemMeta();
                        vex_origin_info4_meta.setDisplayName(UNDERLINE + "Feared Spirit");
                        ArrayList<String> vex_origin_info4_lore = new ArrayList<>();
                        vex_origin_info4_lore.add(WHITE + "Villagers will not trade with you");
                        vex_origin_info4_meta.setLore(vex_origin_info4_lore);
                        vex_origin_info4.setItemMeta(vex_origin_info4_meta);

                        ItemMeta vex_origin_info5_meta = vex_origin_info5.getItemMeta();
                        vex_origin_info5_meta.setDisplayName(UNDERLINE + "Unholy Creature");
                        ArrayList<String> vex_origin_info5_lore = new ArrayList<>();
                        vex_origin_info5_lore.add(WHITE + "You cannot wear armour made of iron, gold, or chainmail");
                        vex_origin_info5_meta.setLore(vex_origin_info5_lore);
                        vex_origin_info5.setItemMeta(vex_origin_info5_meta);

                        ItemMeta vex_origin_info6_meta = vex_origin_info6.getItemMeta();
                        vex_origin_info6_meta.setDisplayName(UNDERLINE + "Bloodlust");
                        ArrayList<String> vex_origin_info6_lore = new ArrayList<>();
                        vex_origin_info6_lore.add(WHITE + "You can only eat raw meat");
                        vex_origin_info6_meta.setLore(vex_origin_info6_lore);
                        vex_origin_info6.setItemMeta(vex_origin_info6_meta);

                        ItemMeta vex_origin_info7_meta = vex_origin_info7.getItemMeta();
                        vex_origin_info7_meta.setDisplayName(UNDERLINE + "Little Demon");
                        ArrayList<String> vex_origin_info7_lore = new ArrayList<>();
                        vex_origin_info7_lore.add(WHITE + "You are slower and MUCH weaker when in water");
                        vex_origin_info7_meta.setLore(vex_origin_info7_lore);
                        vex_origin_info7.setItemMeta(vex_origin_info7_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta vex_meta = vex.getItemMeta();
                        vex_meta.setDisplayName("Vexian");
                        ArrayList<String> vex_lore = new ArrayList<>();
                        vex_lore.add(AQUA + "Vex Origin");
                        vex_meta.setLore(vex_lore);
                        vex.setItemMeta(vex_meta);


                        ItemStack[] vexgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, vex, air, air, air, air, air, air, vex_origin_info1, vex_origin_info2, vex_origin_info3, vex_origin_info4, vex_origin_info5, air, air, air, air, vex_origin_info6, vex_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        vexgui.setContents(vexgui_items);
                        p.openInventory(vexgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-blazeborn")){
                        Inventory blazegui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");

                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack blaze = new ItemStack(Material.BLAZE_POWDER);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack blaze_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack blaze_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack blaze_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack blaze_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack blaze_origin_info5 = new ItemStack(Material.FILLED_MAP);
                        ItemStack blaze_origin_info6 = new ItemStack(Material.FILLED_MAP);
                        ItemStack blaze_origin_info7 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta blaze_origin_info1_meta = blaze_origin_info1.getItemMeta();
                        blaze_origin_info1_meta.setDisplayName(UNDERLINE + "Born from Flames");
                        ArrayList<String> blaze_origin_info1_lore = new ArrayList<>();
                        blaze_origin_info1_lore.add(WHITE + "Your natural spawn is in the Nether");
                        blaze_origin_info1_meta.setLore(blaze_origin_info1_lore);
                        blaze_origin_info1.setItemMeta(blaze_origin_info1_meta);

                        ItemMeta blaze_origin_info2_meta = blaze_origin_info2.getItemMeta();
                        blaze_origin_info2_meta.setDisplayName(UNDERLINE + "Burning Wrath");
                        ArrayList<String> blaze_origin_info2_lore = new ArrayList<>();
                        blaze_origin_info2_lore.add(WHITE + "When on fire, you deal additional damage");
                        blaze_origin_info2_meta.setLore(blaze_origin_info2_lore);
                        blaze_origin_info2.setItemMeta(blaze_origin_info2_meta);

                        ItemMeta blaze_origin_info3_meta = blaze_origin_info3.getItemMeta();
                        blaze_origin_info3_meta.setDisplayName(UNDERLINE + "Fire Immunity");
                        ArrayList<String> blaze_origin_info3_lore = new ArrayList<>();
                        blaze_origin_info3_lore.add(WHITE + "You are immune to all types of fire damage");
                        blaze_origin_info3_meta.setLore(blaze_origin_info3_lore);
                        blaze_origin_info3.setItemMeta(blaze_origin_info3_meta);

                        ItemMeta blaze_origin_info4_meta = blaze_origin_info4.getItemMeta();
                        blaze_origin_info4_meta.setDisplayName(UNDERLINE + "To Hot for.. Uh.. Ya.. Water?");
                        ArrayList<String> blaze_origin_info4_lore = new ArrayList<>();
                        blaze_origin_info4_lore.add(WHITE + "You damage while in water, and Merlings deal more damage to you");
                        blaze_origin_info4_meta.setLore(blaze_origin_info4_lore);
                        blaze_origin_info4.setItemMeta(blaze_origin_info4_meta);

                        ItemMeta blaze_origin_info5_meta = blaze_origin_info5.getItemMeta();
                        blaze_origin_info5_meta.setDisplayName(UNDERLINE + "Hotblooded");
                        ArrayList<String> blaze_origin_info5_lore = new ArrayList<>();
                        blaze_origin_info5_lore.add(WHITE + "Due to your hot body, venom burns up, making you immune to poison");
                        blaze_origin_info5_meta.setLore(blaze_origin_info5_lore);
                        blaze_origin_info5.setItemMeta(blaze_origin_info5_meta);

                        ItemMeta blaze_origin_info6_meta = blaze_origin_info6.getItemMeta();
                        blaze_origin_info6_meta.setDisplayName(UNDERLINE + "Opposite Forces");
                        ArrayList<String> blaze_origin_info6_lore = new ArrayList<>();
                        blaze_origin_info6_lore.add(WHITE + "You are much weaker in colder biomes and at high altitudes");
                        blaze_origin_info6_meta.setLore(blaze_origin_info6_lore);
                        blaze_origin_info6.setItemMeta(blaze_origin_info6_meta);

                        ItemMeta blaze_origin_info7_meta = blaze_origin_info7.getItemMeta();
                        blaze_origin_info7_meta.setDisplayName(UNDERLINE + "Flames of the Nether");
                        ArrayList<String> blaze_origin_info7_lore = new ArrayList<>();
                        blaze_origin_info7_lore.add(WHITE + "Upon hitting someone, they are set on fire");
                        blaze_origin_info7_meta.setLore(blaze_origin_info7_lore);
                        blaze_origin_info7.setItemMeta(blaze_origin_info7_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta blaze_meta = blaze.getItemMeta();
                        blaze_meta.setDisplayName("Blazeborn");
                        ArrayList<String> blaze_lore = new ArrayList<>();
                        blaze_lore.add(GOLD + "Blaze Origin");
                        blaze_meta.setLore(blaze_lore);
                        blaze.setItemMeta(blaze_meta);


                        ItemStack[] blazegui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, blaze, air, air, air, air, air, air, blaze_origin_info1, blaze_origin_info2, blaze_origin_info3, blaze_origin_info4, blaze_origin_info5, air, air, air, air, blaze_origin_info6, blaze_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        blazegui.setContents(blazegui_items);
                        p.openInventory(blazegui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-starborn")){
                        Inventory stargui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");

                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack star = new ItemStack(Material.NETHER_STAR);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack star_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack star_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack star_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack star_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack star_origin_info5 = new ItemStack(Material.FILLED_MAP);
                        ItemStack star_origin_info6 = new ItemStack(Material.FILLED_MAP);
                        ItemStack star_origin_info7 = new ItemStack(Material.FILLED_MAP);
                        ItemStack star_origin_info8 = new ItemStack(Material.FILLED_MAP);
                        ItemStack star_origin_info9 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta star_origin_info1_meta = star_origin_info1.getItemMeta();
                        star_origin_info1_meta.setDisplayName(UNDERLINE + "Wanderer of the Stars");
                        ArrayList<String> star_origin_info1_lore = new ArrayList<>();
                        star_origin_info1_lore.add(WHITE + "You cannot sleep at night");
                        star_origin_info1_meta.setLore(star_origin_info1_lore);
                        star_origin_info1.setItemMeta(star_origin_info1_meta);

                        ItemMeta star_origin_info2_meta = star_origin_info2.getItemMeta();
                        star_origin_info2_meta.setDisplayName(UNDERLINE + "Shooting Star");
                        ArrayList<String> star_origin_info2_lore = new ArrayList<>();
                        star_origin_info2_lore.add(WHITE + "You can fling yourself into the air after a 5 second cooldown");
                        star_origin_info2_meta.setLore(star_origin_info2_lore);
                        star_origin_info2.setItemMeta(star_origin_info2_meta);

                        ItemMeta star_origin_info3_meta = star_origin_info3.getItemMeta();
                        star_origin_info3_meta.setDisplayName(UNDERLINE + "Falling Stars");
                        ArrayList<String> star_origin_info3_lore = new ArrayList<>();
                        star_origin_info3_lore.add(WHITE + "You can drop stars on your enemy every 30 seconds");
                        star_origin_info3_meta.setLore(star_origin_info3_lore);
                        star_origin_info3.setItemMeta(star_origin_info3_meta);

                        ItemMeta star_origin_info4_meta = star_origin_info4.getItemMeta();
                        star_origin_info4_meta.setDisplayName(UNDERLINE + "Mysterious Power");
                        ArrayList<String> star_origin_info4_lore = new ArrayList<>();
                        star_origin_info4_lore.add(WHITE + "When night falls, you have will be granted a special gift from the stars above");
                        star_origin_info4_meta.setLore(star_origin_info4_lore);
                        star_origin_info4.setItemMeta(star_origin_info4_meta);

                        ItemMeta star_origin_info5_meta = star_origin_info5.getItemMeta();
                        star_origin_info5_meta.setDisplayName(UNDERLINE + "Supernova");
                        ArrayList<String> star_origin_info5_lore = new ArrayList<>();
                        star_origin_info5_lore.add(WHITE + "When you die, you explode into a supernova");
                        star_origin_info5_meta.setLore(star_origin_info5_lore);
                        star_origin_info5.setItemMeta(star_origin_info5_meta);

                        ItemMeta star_origin_info6_meta = star_origin_info6.getItemMeta();
                        star_origin_info6_meta.setDisplayName(UNDERLINE + "Cold Vacuum");
                        ArrayList<String> star_origin_info6_lore = new ArrayList<>();
                        star_origin_info6_lore.add(WHITE + "You are used to the coldness of space, so you take double damage from fire");
                        star_origin_info6_meta.setLore(star_origin_info6_lore);
                        star_origin_info6.setItemMeta(star_origin_info6_meta);

                        ItemMeta star_origin_info7_meta = star_origin_info7.getItemMeta();
                        star_origin_info7_meta.setDisplayName(UNDERLINE + "Stargazer");
                        ArrayList<String> star_origin_info7_lore = new ArrayList<>();
                        star_origin_info7_lore.add(WHITE + "When exposed to the stars, you gain speed and regeneration, as a gift from the stars");
                        star_origin_info7_meta.setLore(star_origin_info7_lore);
                        star_origin_info7.setItemMeta(star_origin_info7_meta);

                        ItemMeta star_origin_info8_meta = star_origin_info8.getItemMeta();
                        star_origin_info8_meta.setDisplayName(UNDERLINE + "Unknown Realms");
                        ArrayList<String> star_origin_info8_lore = new ArrayList<>();
                        star_origin_info8_lore.add(WHITE + "Being in a realm without stars makes you weaker");
                        star_origin_info8_meta.setLore(star_origin_info8_lore);
                        star_origin_info8.setItemMeta(star_origin_info8_meta);

                        ItemMeta star_origin_info9_meta = star_origin_info9.getItemMeta();
                        star_origin_info9_meta.setDisplayName(UNDERLINE + "Nonviolent");
                        ArrayList<String> star_origin_info9_lore = new ArrayList<>();
                        star_origin_info9_lore.add(WHITE + "You have a chance to be imobilized upon taking damage, and your a vegitarian");
                        star_origin_info9_meta.setLore(star_origin_info9_lore);
                        star_origin_info9.setItemMeta(star_origin_info9_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta star_meta = star.getItemMeta();
                        star_meta.setDisplayName("Starborne");
                        ArrayList<String> star_lore = new ArrayList<>();
                        star_lore.add(LIGHT_PURPLE + "Starborne Origin");
                        star_meta.setLore(star_lore);
                        star.setItemMeta(star_meta);


                        ItemStack[] stargui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, star, air, air, air, air, air, air, star_origin_info1, star_origin_info2, star_origin_info3, star_origin_info4, star_origin_info5, air, air, air, air, star_origin_info6, star_origin_info7, star_origin_info8, star_origin_info9, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        stargui.setContents(stargui_items);
                        p.openInventory(stargui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-merling")){
                        Inventory mermaidgui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack mermaid = new ItemStack(Material.COD);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);

                        ItemStack mermaid_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack mermaid_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack mermaid_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack mermaid_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack mermaid_origin_info5 = new ItemStack(Material.FILLED_MAP);
                        ItemStack mermaid_origin_info6 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta mermaid_origin_info1_meta = mermaid_origin_info1.getItemMeta();
                        mermaid_origin_info1_meta.setDisplayName(UNDERLINE + "Gills");
                        ArrayList<String> mermaid_origin_info1_lore = new ArrayList<>();
                        mermaid_origin_info1_lore.add(WHITE + "You can ONLY breathe underwater, when raining, you can breathe on land for a short time");
                        mermaid_origin_info1_meta.setLore(mermaid_origin_info1_lore);
                        mermaid_origin_info1.setItemMeta(mermaid_origin_info1_meta);

                        ItemMeta mermaid_origin_info2_meta = mermaid_origin_info2.getItemMeta();
                        mermaid_origin_info2_meta.setDisplayName(UNDERLINE + "Wet Eyes");
                        ArrayList<String> mermaid_origin_info2_lore = new ArrayList<>();
                        mermaid_origin_info2_lore.add(WHITE + "Your vision underwater is nearly perfect");
                        mermaid_origin_info2_meta.setLore(mermaid_origin_info2_lore);
                        mermaid_origin_info2.setItemMeta(mermaid_origin_info2_meta);

                        ItemMeta mermaid_origin_info3_meta = mermaid_origin_info3.getItemMeta();
                        mermaid_origin_info3_meta.setDisplayName(UNDERLINE + "Opposing Forces");
                        ArrayList<String> mermaid_origin_info3_lore = new ArrayList<>();
                        mermaid_origin_info3_lore.add(WHITE + "You take significantly more damage from fire");
                        mermaid_origin_info3_meta.setLore(mermaid_origin_info3_lore);
                        mermaid_origin_info3.setItemMeta(mermaid_origin_info3_meta);

                        ItemMeta mermaid_origin_info4_meta = mermaid_origin_info4.getItemMeta();
                        mermaid_origin_info4_meta.setDisplayName(UNDERLINE + "Fins");
                        ArrayList<String> mermaid_origin_info4_lore = new ArrayList<>();
                        mermaid_origin_info4_lore.add(WHITE + "You can swim much faster underwater, and don't sink underwater");
                        mermaid_origin_info4_meta.setLore(mermaid_origin_info4_lore);
                        mermaid_origin_info4.setItemMeta(mermaid_origin_info4_meta);

                        ItemMeta mermaid_origin_info5_meta = mermaid_origin_info5.getItemMeta();
                        mermaid_origin_info5_meta.setDisplayName(UNDERLINE + "please don't");
                        ArrayList<String> mermaid_origin_info5_lore = new ArrayList<>();
                        mermaid_origin_info5_lore.add(WHITE + "don't eat fish, its basically cannabalism and thats gross. It gives you nausea.");
                        mermaid_origin_info5_meta.setLore(mermaid_origin_info5_lore);
                        mermaid_origin_info5.setItemMeta(mermaid_origin_info5_meta);

                        ItemMeta mermaid_origin_info6_meta = mermaid_origin_info6.getItemMeta();
                        mermaid_origin_info6_meta.setDisplayName(UNDERLINE + "Luck of the Sea");
                        ArrayList<String> mermaid_origin_info6_lore = new ArrayList<>();
                        mermaid_origin_info6_lore.add(WHITE + "You have increased fishing luck.");
                        mermaid_origin_info6_meta.setLore(mermaid_origin_info6_lore);
                        mermaid_origin_info6.setItemMeta(mermaid_origin_info6_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta mermaid_meta = mermaid.getItemMeta();
                        mermaid_meta.setDisplayName("Merling");
                        ArrayList<String> mermaid_lore = new ArrayList<>();
                        mermaid_lore.add(BLUE + "Merling Origin");
                        mermaid_meta.setLore(mermaid_lore);
                        mermaid.setItemMeta(mermaid_meta);


                        ItemStack[] mermaidgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, mermaid, air, air, air, air, air, air, mermaid_origin_info1, mermaid_origin_info2, mermaid_origin_info3, mermaid_origin_info4, mermaid_origin_info5, air, air, air, air, mermaid_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        mermaidgui.setContents(mermaidgui_items);
                        p.openInventory(mermaidgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-allay")){
                        Inventory allaygui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");

                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack allay = new ItemStack(Material.AMETHYST_SHARD);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack blank = new ItemStack(Material.PAPER);

                        ItemStack allay_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack allay_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack allay_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack allay_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack allay_origin_info5 = new ItemStack(Material.FILLED_MAP);
                        ItemStack allay_origin_info6 = new ItemStack(Material.FILLED_MAP);
                        ItemStack allay_origin_info7 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta allay_origin_info1_meta = allay_origin_info1.getItemMeta();
                        allay_origin_info1_meta.setDisplayName(UNDERLINE + "Little Fairy");
                        ArrayList<String> allay_origin_info1_lore = new ArrayList<>();
                        allay_origin_info1_lore.add(WHITE + "You have small wings, you can fly and float");
                        allay_origin_info1_meta.setLore(allay_origin_info1_lore);
                        allay_origin_info1.setItemMeta(allay_origin_info1_meta);

                        ItemMeta allay_origin_info2_meta = allay_origin_info2.getItemMeta();
                        allay_origin_info2_meta.setDisplayName(UNDERLINE + "Blue Spirit");
                        ArrayList<String> allay_origin_info2_lore = new ArrayList<>();
                        allay_origin_info2_lore.add(WHITE + "You are semi-translucent, half height, and glow in dark places. Also you're blue");
                        allay_origin_info2_meta.setLore(allay_origin_info2_lore);
                        allay_origin_info2.setItemMeta(allay_origin_info2_meta);

                        ItemMeta allay_origin_info3_meta = allay_origin_info3.getItemMeta();
                        allay_origin_info3_meta.setDisplayName(UNDERLINE + "Sounds of Music");
                        ArrayList<String> allay_origin_info3_lore = new ArrayList<>();
                        allay_origin_info3_lore.add(WHITE + "You enjoy the sounds of music, and can use a jukebox as a respawn anchor");
                        allay_origin_info3_meta.setLore(allay_origin_info3_lore);
                        allay_origin_info3.setItemMeta(allay_origin_info3_meta);

                        ItemMeta allay_origin_info4_meta = allay_origin_info4.getItemMeta();
                        allay_origin_info4_meta.setDisplayName(UNDERLINE + "COOKIES");
                        ArrayList<String> allay_origin_info4_lore = new ArrayList<>();
                        allay_origin_info4_lore.add(WHITE + "Cookies give the same saturation as steak");
                        allay_origin_info4_meta.setLore(allay_origin_info4_lore);
                        allay_origin_info4.setItemMeta(allay_origin_info4_meta);

                        ItemMeta allay_origin_info5_meta = allay_origin_info5.getItemMeta();
                        allay_origin_info5_meta.setDisplayName(UNDERLINE + "Treasure Finder");
                        ArrayList<String> allay_origin_info5_lore = new ArrayList<>();
                        allay_origin_info5_lore.add(WHITE + "You have increased chances of getting treasure loot and villagers will lower their prices for you");
                        allay_origin_info5_meta.setLore(allay_origin_info5_lore);
                        allay_origin_info5.setItemMeta(allay_origin_info5_meta);

                        ItemMeta allay_origin_info6_meta = allay_origin_info6.getItemMeta();
                        allay_origin_info6_meta.setDisplayName(UNDERLINE + "Kinda Flamable");
                        ArrayList<String> allay_origin_info6_lore = new ArrayList<>();
                        allay_origin_info6_lore.add(WHITE + "You burn easily, you take extra fire damage and have half health");
                        allay_origin_info6_meta.setLore(allay_origin_info6_lore);
                        allay_origin_info6.setItemMeta(allay_origin_info6_meta);

                        ItemMeta allay_origin_info7_meta = allay_origin_info7.getItemMeta();
                        allay_origin_info7_meta.setDisplayName(UNDERLINE + "Friendly Angel");
                        ArrayList<String> allay_origin_info7_lore = new ArrayList<>();
                        allay_origin_info7_lore.add(WHITE + "You don't like to harm animals, you get nauseous when eating meat");
                        allay_origin_info7_meta.setLore(allay_origin_info7_lore);
                        allay_origin_info7.setItemMeta(allay_origin_info7_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta allay_meta = allay.getItemMeta();
                        allay_meta.setDisplayName("Allay");
                        ArrayList<String> allay_lore = new ArrayList<>();
                        allay_lore.add(AQUA + "Allay Origin");
                        allay_meta.setLore(allay_lore);
                        allay.setItemMeta(allay_meta);


                        ItemStack[] allaygui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, allay, air, air, air, air, air, air, allay_origin_info1, allay_origin_info2, allay_origin_info3, allay_origin_info4, allay_origin_info5, air, air, air, air, allay_origin_info6, allay_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        allaygui.setContents(allaygui_items);
                        p.openInventory(allaygui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-rabbit")){
                        Inventory rabbitgui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");

                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack rabbit = new ItemStack(Material.CARROT);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack rabbit_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack rabbit_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack rabbit_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack rabbit_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack rabbit_origin_info5 = new ItemStack(Material.FILLED_MAP);
                        ItemStack rabbit_origin_info6 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta rabbit_origin_info1_meta = rabbit_origin_info1.getItemMeta();
                        rabbit_origin_info1_meta.setDisplayName(UNDERLINE + "Leap");
                        ArrayList<String> rabbit_origin_info1_lore = new ArrayList<>();
                        rabbit_origin_info1_lore.add(WHITE + "You leap in the direction you're looking to");
                        rabbit_origin_info1_meta.setLore(rabbit_origin_info1_lore);
                        rabbit_origin_info1.setItemMeta(rabbit_origin_info1_meta);

                        ItemMeta rabbit_origin_info2_meta = rabbit_origin_info2.getItemMeta();
                        rabbit_origin_info2_meta.setDisplayName(UNDERLINE + "Strong Hopper");
                        ArrayList<String> rabbit_origin_info2_lore = new ArrayList<>();
                        rabbit_origin_info2_lore.add(WHITE + "You jump significantly higher");
                        rabbit_origin_info2_meta.setLore(rabbit_origin_info2_lore);
                        rabbit_origin_info2.setItemMeta(rabbit_origin_info2_meta);

                        ItemMeta rabbit_origin_info3_meta = rabbit_origin_info3.getItemMeta();
                        rabbit_origin_info3_meta.setDisplayName(UNDERLINE + "Shock Absorption");
                        ArrayList<String> rabbit_origin_info3_lore = new ArrayList<>();
                        rabbit_origin_info3_lore.add(WHITE + "You take less fall damage");
                        rabbit_origin_info3_meta.setLore(rabbit_origin_info3_lore);
                        rabbit_origin_info3.setItemMeta(rabbit_origin_info3_meta);

                        ItemMeta rabbit_origin_info4_meta = rabbit_origin_info4.getItemMeta();
                        rabbit_origin_info4_meta.setDisplayName(UNDERLINE + "Delicious");
                        ArrayList<String> rabbit_origin_info4_lore = new ArrayList<>();
                        rabbit_origin_info4_lore.add(WHITE + "You may drop a rabbit's foot when hit");
                        rabbit_origin_info4_meta.setLore(rabbit_origin_info4_lore);
                        rabbit_origin_info4.setItemMeta(rabbit_origin_info4_meta);

                        ItemMeta rabbit_origin_info5_meta = rabbit_origin_info5.getItemMeta();
                        rabbit_origin_info5_meta.setDisplayName(UNDERLINE + "Picky Eater");
                        ArrayList<String> rabbit_origin_info5_lore = new ArrayList<>();
                        rabbit_origin_info5_lore.add(WHITE + "You can only eat carrots and golden carrots");
                        rabbit_origin_info5_meta.setLore(rabbit_origin_info5_lore);
                        rabbit_origin_info5.setItemMeta(rabbit_origin_info5_meta);

                        ItemMeta rabbit_origin_info6_meta = rabbit_origin_info6.getItemMeta();
                        rabbit_origin_info6_meta.setDisplayName(UNDERLINE + "Fragile");
                        ArrayList<String> rabbit_origin_info6_lore = new ArrayList<>();
                        rabbit_origin_info6_lore.add(WHITE + "You have 3 less hearts");
                        rabbit_origin_info6_meta.setLore(rabbit_origin_info6_lore);
                        rabbit_origin_info6.setItemMeta(rabbit_origin_info6_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta rabbit_meta = rabbit.getItemMeta();
                        rabbit_meta.setDisplayName("Rabbit");
                        ArrayList<String> rabbit_lore = new ArrayList<>();
                        rabbit_lore.add(GOLD + "Bunny Origin");
                        rabbit_meta.setLore(rabbit_lore);
                        rabbit.setItemMeta(rabbit_meta);


                        ItemStack[] rabbitgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, rabbit, air, air, air, air, air, air, rabbit_origin_info1, rabbit_origin_info2, rabbit_origin_info3, rabbit_origin_info4, rabbit_origin_info5, air, air, air, air, rabbit_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        rabbitgui.setContents(rabbitgui_items);
                        p.openInventory(rabbitgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-bee")){
                        Inventory beegui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");

                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack bee = new ItemStack(Material.HONEYCOMB);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack bee_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack bee_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack bee_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack bee_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack bee_origin_info5 = new ItemStack(Material.FILLED_MAP);
                        ItemStack bee_origin_info6 = new ItemStack(Material.FILLED_MAP);
                        ItemStack bee_origin_info7 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta bee_origin_info1_meta = bee_origin_info1.getItemMeta();
                        bee_origin_info1_meta.setDisplayName(UNDERLINE + "Featherweight");
                        ArrayList<String> bee_origin_info1_lore = new ArrayList<>();
                        bee_origin_info1_lore.add(WHITE + "You fall as gently to the ground as a feather would unless you shift");
                        bee_origin_info1_meta.setLore(bee_origin_info1_lore);
                        bee_origin_info1.setItemMeta(bee_origin_info1_meta);

                        ItemMeta bee_origin_info2_meta = bee_origin_info2.getItemMeta();
                        bee_origin_info2_meta.setDisplayName(UNDERLINE + "Poisonous");
                        ArrayList<String> bee_origin_info2_lore = new ArrayList<>();
                        bee_origin_info2_lore.add(WHITE + "Hitting someone gives them poison for 2 seconds");
                        bee_origin_info2_meta.setLore(bee_origin_info2_lore);
                        bee_origin_info2.setItemMeta(bee_origin_info2_meta);

                        ItemMeta bee_origin_info3_meta = bee_origin_info3.getItemMeta();
                        bee_origin_info3_meta.setDisplayName(UNDERLINE + "Bloom");
                        ArrayList<String> bee_origin_info3_lore = new ArrayList<>();
                        bee_origin_info3_lore.add(WHITE + "You gain regeneration when near flowers");
                        bee_origin_info3_meta.setLore(bee_origin_info3_lore);
                        bee_origin_info3.setItemMeta(bee_origin_info3_meta);

                        ItemMeta bee_origin_info4_meta = bee_origin_info4.getItemMeta();
                        bee_origin_info4_meta.setDisplayName(UNDERLINE + "Flight");
                        ArrayList<String> bee_origin_info4_lore = new ArrayList<>();
                        bee_origin_info4_lore.add(WHITE + "You can fly, just like a bee!(WHATT)");
                        bee_origin_info4_meta.setLore(bee_origin_info4_lore);
                        bee_origin_info4.setItemMeta(bee_origin_info4_meta);

                        ItemMeta bee_origin_info5_meta = bee_origin_info5.getItemMeta();
                        bee_origin_info5_meta.setDisplayName(UNDERLINE + "Nighttime");
                        ArrayList<String> bee_origin_info5_lore = new ArrayList<>();
                        bee_origin_info5_lore.add(WHITE + "You are sleepy at night, so you walk and fly slower");
                        bee_origin_info5_meta.setLore(bee_origin_info5_lore);
                        bee_origin_info5.setItemMeta(bee_origin_info5_meta);

                        ItemMeta bee_origin_info6_meta = bee_origin_info6.getItemMeta();
                        bee_origin_info6_meta.setDisplayName(UNDERLINE + "Lifespan");
                        ArrayList<String> bee_origin_info6_lore = new ArrayList<>();
                        bee_origin_info6_lore.add(WHITE + "You have 3 less hearts");
                        bee_origin_info6_meta.setLore(bee_origin_info6_lore);
                        bee_origin_info6.setItemMeta(bee_origin_info6_meta);

                        ItemMeta bee_origin_info7_meta = bee_origin_info7.getItemMeta();
                        bee_origin_info7_meta.setDisplayName(UNDERLINE + "Rain");
                        ArrayList<String> bee_origin_info7_lore = new ArrayList<>();
                        bee_origin_info7_lore.add(WHITE + "You cannot fly when in the rain and are weaker while wet");
                        bee_origin_info7_meta.setLore(bee_origin_info7_lore);
                        bee_origin_info7.setItemMeta(bee_origin_info7_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta bee_meta = bee.getItemMeta();
                        bee_meta.setDisplayName("Bumblebee");
                        ArrayList<String> bee_lore = new ArrayList<>();
                        bee_lore.add(YELLOW + "Bee Origin");
                        bee_meta.setLore(bee_lore);
                        bee.setItemMeta(bee_meta);


                        ItemStack[] beegui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, bee, air, air, air, air, air, air, bee_origin_info1, bee_origin_info2, bee_origin_info3, bee_origin_info4, bee_origin_info5, air, air, air, air, bee_origin_info6, bee_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        beegui.setContents(beegui_items);
                        p.openInventory(beegui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-elytrian")){
                        Inventory elyrtiangui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");

                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack elyrtian = new ItemStack(Material.ELYTRA);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack elyrtian_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack elyrtian_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack elyrtian_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack elyrtian_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack elyrtian_origin_info5 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta elyrtian_origin_info1_meta = elyrtian_origin_info1.getItemMeta();
                        elyrtian_origin_info1_meta.setDisplayName(UNDERLINE + "Winged");
                        ArrayList<String> elyrtian_origin_info1_lore = new ArrayList<>();
                        elyrtian_origin_info1_lore.add(WHITE + "You have Elytra wings without needing to equip any");
                        elyrtian_origin_info1_meta.setLore(elyrtian_origin_info1_lore);
                        elyrtian_origin_info1.setItemMeta(elyrtian_origin_info1_meta);

                        ItemMeta elyrtian_origin_info2_meta = elyrtian_origin_info2.getItemMeta();
                        elyrtian_origin_info2_meta.setDisplayName(UNDERLINE + "Gift of the Winds");
                        ArrayList<String> elyrtian_origin_info2_lore = new ArrayList<>();
                        elyrtian_origin_info2_lore.add(WHITE + "Every 60 seconds, you can launch yourself 20 blocks in the air");
                        elyrtian_origin_info2_meta.setLore(elyrtian_origin_info2_lore);
                        elyrtian_origin_info2.setItemMeta(elyrtian_origin_info2_meta);

                        ItemMeta elyrtian_origin_info3_meta = elyrtian_origin_info3.getItemMeta();
                        elyrtian_origin_info3_meta.setDisplayName(UNDERLINE + "Claustrophobia");
                        ArrayList<String> elyrtian_origin_info3_lore = new ArrayList<>();
                        elyrtian_origin_info3_lore.add(WHITE + "Being somewhere with a low ceiling for too long will weaken you");
                        elyrtian_origin_info3_meta.setLore(elyrtian_origin_info3_lore);
                        elyrtian_origin_info3.setItemMeta(elyrtian_origin_info3_meta);

                        ItemMeta elyrtian_origin_info4_meta = elyrtian_origin_info4.getItemMeta();
                        elyrtian_origin_info4_meta.setDisplayName(UNDERLINE + "Need for Mobility");
                        ArrayList<String> elyrtian_origin_info4_lore = new ArrayList<>();
                        elyrtian_origin_info4_lore.add(WHITE + "You cannot wear any heavy armour with prot values higher than chainmail");
                        elyrtian_origin_info4_meta.setLore(elyrtian_origin_info4_lore);
                        elyrtian_origin_info4.setItemMeta(elyrtian_origin_info4_meta);

                        ItemMeta elyrtian_origin_info5_meta = elyrtian_origin_info5.getItemMeta();
                        elyrtian_origin_info5_meta.setDisplayName(UNDERLINE + "Brittle Bones");
                        ArrayList<String> elyrtian_origin_info5_lore = new ArrayList<>();
                        elyrtian_origin_info5_lore.add(WHITE + "You take more damage from falling and flying into blocks");
                        elyrtian_origin_info5_meta.setLore(elyrtian_origin_info5_lore);
                        elyrtian_origin_info5.setItemMeta(elyrtian_origin_info5_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta elyrtian_meta = elyrtian.getItemMeta();
                        elyrtian_meta.setDisplayName("Elytrian");
                        ArrayList<String> elyrtian_lore = new ArrayList<>();
                        elyrtian_lore.add(GRAY + "Elytrian Origin");
                        elyrtian_meta.setLore(elyrtian_lore);
                        elyrtian.setItemMeta(elyrtian_meta);


                        ItemStack[] elyrtiangui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, elyrtian, air, air, air, air, air, air, elyrtian_origin_info1, elyrtian_origin_info2, elyrtian_origin_info3, elyrtian_origin_info4, elyrtian_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        elyrtiangui.setContents(elyrtiangui_items);
                        p.openInventory(elyrtiangui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-avian")){
                        Inventory aviangui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");

                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack avian = new ItemStack(Material.FEATHER);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack avian_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack avian_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack avian_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack avian_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack avian_origin_info5 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta avian_origin_info1_meta = avian_origin_info1.getItemMeta();
                        avian_origin_info1_meta.setDisplayName(UNDERLINE + "Featherweight");
                        ArrayList<String> avian_origin_info1_lore = new ArrayList<>();
                        avian_origin_info1_lore.add(WHITE + "You fall as gently to the ground as a feather would, unless you shift");
                        avian_origin_info1_meta.setLore(avian_origin_info1_lore);
                        avian_origin_info1.setItemMeta(avian_origin_info1_meta);

                        ItemMeta avian_origin_info2_meta = avian_origin_info2.getItemMeta();
                        avian_origin_info2_meta.setDisplayName(UNDERLINE + "Tailwind");
                        ArrayList<String> avian_origin_info2_lore = new ArrayList<>();
                        avian_origin_info2_lore.add(WHITE + "You are a little quicker on foot than others");
                        avian_origin_info2_meta.setLore(avian_origin_info2_lore);
                        avian_origin_info2.setItemMeta(avian_origin_info2_meta);

                        ItemMeta avian_origin_info3_meta = avian_origin_info3.getItemMeta();
                        avian_origin_info3_meta.setDisplayName(UNDERLINE + "Oviparous");
                        ArrayList<String> avian_origin_info3_lore = new ArrayList<>();
                        avian_origin_info3_lore.add(WHITE + "Whenever you wake up in the morning, you lay an egg");
                        avian_origin_info3_meta.setLore(avian_origin_info3_lore);
                        avian_origin_info3.setItemMeta(avian_origin_info3_meta);

                        ItemMeta avian_origin_info4_meta = avian_origin_info4.getItemMeta();
                        avian_origin_info4_meta.setDisplayName(UNDERLINE + "Vegetarian");
                        ArrayList<String> avian_origin_info4_lore = new ArrayList<>();
                        avian_origin_info4_lore.add(WHITE + "You can't digest any meat");
                        avian_origin_info4_meta.setLore(avian_origin_info4_lore);
                        avian_origin_info4.setItemMeta(avian_origin_info4_meta);

                        ItemMeta avian_origin_info5_meta = avian_origin_info5.getItemMeta();
                        avian_origin_info5_meta.setDisplayName(UNDERLINE + "Fresh Air");
                        ArrayList<String> avian_origin_info5_lore = new ArrayList<>();
                        avian_origin_info5_lore.add(WHITE + "When sleeping, your bed needs to be at an altitude of at least 100 blocks");
                        avian_origin_info5_meta.setLore(avian_origin_info5_lore);
                        avian_origin_info5.setItemMeta(avian_origin_info5_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta avian_meta = avian.getItemMeta();
                        avian_meta.setDisplayName("Avian");
                        ArrayList<String> avian_lore = new ArrayList<>();
                        avian_lore.add(DARK_AQUA + "Avian Origin");
                        avian_meta.setLore(avian_lore);
                        avian.setItemMeta(avian_meta);


                        ItemStack[] aviangui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, avian, air, air, air, air, air, air, avian_origin_info1, avian_origin_info2, avian_origin_info3, avian_origin_info4, avian_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        aviangui.setContents(aviangui_items);
                        p.openInventory(aviangui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-piglin")){
                        Inventory piglingui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack piglin = new ItemStack(Material.GOLD_INGOT);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);

                        ItemStack piglin_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack piglin_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack piglin_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack piglin_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack piglin_origin_info5 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta piglin_origin_info1_meta = piglin_origin_info1.getItemMeta();
                        piglin_origin_info1_meta.setDisplayName(UNDERLINE + "I like to be SHINY");
                        ArrayList<String> piglin_origin_info1_lore = new ArrayList<>();
                        piglin_origin_info1_lore.add(WHITE + "Golden tools deal extra damage and gold armour has more protection");
                        piglin_origin_info1_meta.setLore(piglin_origin_info1_lore);
                        piglin_origin_info1.setItemMeta(piglin_origin_info1_meta);

                        ItemMeta piglin_origin_info2_meta = piglin_origin_info2.getItemMeta();
                        piglin_origin_info2_meta.setDisplayName(UNDERLINE + "Friendly Frenemies");
                        ArrayList<String> piglin_origin_info2_lore = new ArrayList<>();
                        piglin_origin_info2_lore.add(WHITE + "Piglins won't attack you unless provoked, Brutes will still attack on sight");
                        piglin_origin_info2_meta.setLore(piglin_origin_info2_lore);
                        piglin_origin_info2.setItemMeta(piglin_origin_info2_meta);

                        ItemMeta piglin_origin_info3_meta = piglin_origin_info3.getItemMeta();
                        piglin_origin_info3_meta.setDisplayName(UNDERLINE + "Nether Dweller");
                        ArrayList<String> piglin_origin_info3_lore = new ArrayList<>();
                        piglin_origin_info3_lore.add(WHITE + "Your natural spawn is in the Nether and you can only eat meat");
                        piglin_origin_info3_meta.setLore(piglin_origin_info3_lore);
                        piglin_origin_info3.setItemMeta(piglin_origin_info3_meta);

                        ItemMeta piglin_origin_info4_meta = piglin_origin_info4.getItemMeta();
                        piglin_origin_info4_meta.setDisplayName(UNDERLINE + "Colder Realms");
                        ArrayList<String> piglin_origin_info4_lore = new ArrayList<>();
                        piglin_origin_info4_lore.add(WHITE + "When outside of the Nether, you zombify and become immune to fire and slower");
                        piglin_origin_info4_meta.setLore(piglin_origin_info4_lore);
                        piglin_origin_info4.setItemMeta(piglin_origin_info4_meta);

                        ItemMeta piglin_origin_info5_meta = piglin_origin_info5.getItemMeta();
                        piglin_origin_info5_meta.setDisplayName(UNDERLINE + "BLUE FIRE SPOOKY");
                        ArrayList<String> piglin_origin_info5_lore = new ArrayList<>();
                        piglin_origin_info5_lore.add(WHITE + "You are afraid of soul fire, becoming weak when near it");
                        piglin_origin_info5_meta.setLore(piglin_origin_info5_lore);
                        piglin_origin_info5.setItemMeta(piglin_origin_info5_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta piglin_meta = piglin.getItemMeta();
                        piglin_meta.setDisplayName("Piglin");
                        ArrayList<String> piglin_lore = new ArrayList<>();
                        piglin_lore.add(GOLD + "Piglin Origin");
                        piglin_meta.setLore(piglin_lore);
                        piglin.setItemMeta(piglin_meta);


                        ItemStack[] piglingui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, piglin, air, air, air, air, air, air, piglin_origin_info1, piglin_origin_info2, piglin_origin_info3, piglin_origin_info4, piglin_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        piglingui.setContents(piglingui_items);
                        p.openInventory(piglingui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-dragonborn")){
                        Inventory dragongui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");
                        ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                        ItemMeta disconnectmeta = disconnect.getItemMeta();
                        disconnectmeta.setDisplayName(RED + "Disconnect");
                        disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                        disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        ArrayList<String> disconnectlore = new ArrayList<>();
                        disconnectlore.add(RED + "Disconnect player from choosing");
                        disconnectmeta.setLore(disconnectlore);
                        disconnect.setItemMeta(disconnectmeta);
                        ItemStack close = new ItemStack(Material.BARRIER);
                        ItemStack next = new ItemStack(Material.ARROW);
                        ItemStack dragon = new ItemStack(Material.DRAGON_BREATH);
                        ItemStack air = new ItemStack(Material.AIR);
                        ItemStack blank = new ItemStack(Material.PAPER);

                        ItemStack dragon_origin_info1 = new ItemStack(Material.FILLED_MAP);
                        ItemStack dragon_origin_info2 = new ItemStack(Material.FILLED_MAP);
                        ItemStack dragon_origin_info3 = new ItemStack(Material.FILLED_MAP);
                        ItemStack dragon_origin_info4 = new ItemStack(Material.FILLED_MAP);
                        ItemStack dragon_origin_info5 = new ItemStack(Material.FILLED_MAP);
                        ItemStack dragon_origin_info6 = new ItemStack(Material.FILLED_MAP);


                        ItemMeta dragon_origin_info1_meta = dragon_origin_info1.getItemMeta();
                        dragon_origin_info1_meta.setDisplayName(UNDERLINE + "Mighty Wings");
                        ArrayList<String> dragon_origin_info1_lore = new ArrayList<>();
                        dragon_origin_info1_lore.add(WHITE + "You spawn with a permanent Elytra");
                        dragon_origin_info1_meta.setLore(dragon_origin_info1_lore);
                        dragon_origin_info1.setItemMeta(dragon_origin_info1_meta);

                        ItemMeta dragon_origin_info2_meta = dragon_origin_info2.getItemMeta();
                        dragon_origin_info2_meta.setDisplayName(UNDERLINE + "Heart of a Dragon");
                        ArrayList<String> dragon_origin_info2_lore = new ArrayList<>();
                        dragon_origin_info2_lore.add(WHITE + "You have 6 more hearts and can only eat meat");
                        dragon_origin_info2_meta.setLore(dragon_origin_info2_lore);
                        dragon_origin_info2.setItemMeta(dragon_origin_info2_meta);

                        ItemMeta dragon_origin_info3_meta = dragon_origin_info3.getItemMeta();
                        dragon_origin_info3_meta.setDisplayName(UNDERLINE + "Breath of Fire");
                        ArrayList<String> dragon_origin_info3_lore = new ArrayList<>();
                        dragon_origin_info3_lore.add(WHITE + "You can shoot a dragon fireball upon shift-clicking with a 30 second cooldown");
                        dragon_origin_info3_meta.setLore(dragon_origin_info3_lore);
                        dragon_origin_info3.setItemMeta(dragon_origin_info3_meta);

                        ItemMeta dragon_origin_info4_meta = dragon_origin_info4.getItemMeta();
                        dragon_origin_info4_meta.setDisplayName(UNDERLINE + "Sharp Tips");
                        ArrayList<String> dragon_origin_info4_lore = new ArrayList<>();
                        dragon_origin_info4_lore.add(WHITE + "You take more damage from arrows.");
                        dragon_origin_info4_meta.setLore(dragon_origin_info4_lore);
                        dragon_origin_info4.setItemMeta(dragon_origin_info4_meta);

                        ItemMeta dragon_origin_info5_meta = dragon_origin_info5.getItemMeta();
                        dragon_origin_info5_meta.setDisplayName(UNDERLINE + "Resistance");
                        ArrayList<String> dragon_origin_info5_lore = new ArrayList<>();
                        dragon_origin_info5_lore.add(WHITE + "You take no knockback and have extremely tough and protective skin");
                        dragon_origin_info5_meta.setLore(dragon_origin_info5_lore);
                        dragon_origin_info5.setItemMeta(dragon_origin_info5_meta);

                        ItemMeta dragon_origin_info6_meta = dragon_origin_info6.getItemMeta();
                        dragon_origin_info6_meta.setDisplayName(UNDERLINE + "Hot Touch");
                        ArrayList<String> dragon_origin_info6_lore = new ArrayList<>();
                        dragon_origin_info6_lore.add(WHITE + "You can light furnaces with your dragon fireball");
                        dragon_origin_info6_meta.setLore(dragon_origin_info6_lore);
                        dragon_origin_info6.setItemMeta(dragon_origin_info6_meta);

                        ItemMeta close_meta = close.getItemMeta();
                        close_meta.setDisplayName(RED + "Close");
                        ArrayList<String> close_lore = new ArrayList<>();
                        close_lore.add(RED + "Cancel Choosing");
                        close_meta.setLore(close_lore);
                        close.setItemMeta(close_meta);

                        ItemMeta next_meta = next.getItemMeta();
                        next_meta.setDisplayName(BLUE + "MENU");
                        ArrayList<String> next_lore = new ArrayList<>();
                        next_lore.add(WHITE + "All Origins");
                        next_meta.setLore(next_lore);
                        next.setItemMeta(next_meta);

                        ItemMeta dragon_meta = dragon.getItemMeta();
                        dragon_meta.setDisplayName("Dragonborne");
                        ArrayList<String> dragon_lore = new ArrayList<>();
                        dragon_lore.add(DARK_PURPLE + "Dragon Origin");
                        dragon_meta.setLore(dragon_lore);
                        dragon.setItemMeta(dragon_meta);


                        ItemStack[] dragongui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, dragon, air, air, air, air, air, air, dragon_origin_info1, dragon_origin_info2, dragon_origin_info3, dragon_origin_info4, dragon_origin_info5, air, air, air, air, dragon_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air};
                        dragongui.setContents(dragongui_items);
                        p.openInventory(dragongui);
                    }
                }
                if(args[2].equalsIgnoreCase("origintag")){
                    p.sendMessage(p.getName() + "'s OriginID is " + origintag);
                }

            }

        }else{
            p.sendMessage(ChatColor.RED + "Invalid args.");
        }

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

    @EventHandler
    public void OnClickHelpInventory(InventoryClickEvent e){

        if(e.getView().getTitle().equalsIgnoreCase(BLACK + "Help Screen")){
            if(e.getCurrentItem() != null){
                if(e.getCurrentItem().getType().equals(Material.BARRIER)){
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                }
                e.setCancelled(true);
            }
        }

    }
}
