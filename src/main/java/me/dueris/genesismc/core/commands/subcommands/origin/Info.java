package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.choosing.contents.origins.ExpandedOriginContent;
import me.dueris.genesismc.core.choosing.contents.origins.OriginalOriginContent;
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
                    if(origintag.equalsIgnoreCase("genesis:origin-feline")){
                        p.sendMessage(p.getName() + " has the Feline origin");
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
                    Inventory helpgui = Bukkit.createInventory(p, 54, BLACK + "Help Screen");
                    if(origintag.equalsIgnoreCase("genesis:origin-enderian")){
                        helpgui.setContents(OriginalOriginContent.EnderianContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-shulk")){
                        helpgui.setContents(OriginalOriginContent.ShulkContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-human") || origintag.equalsIgnoreCase("genesis:origin-null")){
                        helpgui.setContents(OriginalOriginContent.HumanContents(p));
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-arachnid")){
                        helpgui.setContents(OriginalOriginContent.ArachnidContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-creep")){
                        helpgui.setContents(ExpandedOriginContent.CreepContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-phantom")){
                        helpgui.setContents(OriginalOriginContent.PhantomContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-slimeling")){
                        helpgui.setContents(ExpandedOriginContent.SlimelingContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-vexian")){
                        helpgui.setContents(OriginalOriginContent.FelineContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-blazeborn")){
                        helpgui.setContents(OriginalOriginContent.BlazebornContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-starborn")){
                        helpgui.setContents(ExpandedOriginContent.StarborneContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-merling")){
                        helpgui.setContents(OriginalOriginContent.MerlingContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-allay")){
                        helpgui.setContents(ExpandedOriginContent.AllayContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-rabbit")){
                        helpgui.setContents(ExpandedOriginContent.RabbitContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-bee")){
                        helpgui.setContents(ExpandedOriginContent.BeeContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-elytrian")){
                        helpgui.setContents(OriginalOriginContent.ElytrianContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-avian")){
                        helpgui.setContents(OriginalOriginContent.AvianContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-piglin")){
                        helpgui.setContents(ExpandedOriginContent.PiglinContents());
                        p.openInventory(helpgui);
                    }
                    if(origintag.equalsIgnoreCase("genesis:origin-sculk")){
                        helpgui.setContents(ExpandedOriginContent.SculkContents());
                        p.openInventory(helpgui);
                    }
                }
                if(args[2].equalsIgnoreCase("origintag")){
                    p.sendMessage(p.getName() + "'s OriginTAG is " + origintag);
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
