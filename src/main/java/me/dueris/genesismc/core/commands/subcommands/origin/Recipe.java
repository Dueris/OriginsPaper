package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemProperties;
import static me.dueris.genesismc.core.files.GenesisDataFiles.getOrbCon;

public class Recipe extends SubCommand implements Listener {
    @Override
    public String getName() {
        return "Recipe";
    }

    @Override
    public String getDescription() {
        return "Shows the recipe for the orb of origin";
    }

    @Override
    public String getSyntax() {
        return "/origins recipe";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (!p.hasPermission("genesismc.origins.cmd.recipe")) return;
        if (getOrbCon().get("orb-of-origins-enabled") == null || getOrbCon().get("orb-of-origins-enabled").toString() != "true") {
            p.sendMessage(ChatColor.RED + "Orb of origin crafting has been disabled by a server admin!");
            return;
        }
        @NotNull Inventory custommenu = Bukkit.createInventory(p, 54, "Orb Recipe");
        ItemStack blank = itemProperties(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), null, null, null, null);

        ItemStack orb = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta meta = orb.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.setCustomModelData(00002);
        meta.setDisplayName(GenesisDataFiles.getOrbCon().getString("name"));
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        orb.setItemMeta(meta);

        try {

            ArrayList<ItemStack> contents = new ArrayList<>();
            for (int i = 0; i <= 53; i++) {
                if (i == 10)
                    contents.add(new ItemStack(Material.valueOf(getOrbCon().get("crafting.top.left").toString())));
                else if (i == 11)
                    contents.add(new ItemStack(Material.valueOf(getOrbCon().get("crafting.top.middle").toString())));
                else if (i == 12)
                    contents.add(new ItemStack(Material.valueOf(getOrbCon().get("crafting.top.right").toString())));
                else if (i == 19)
                    contents.add(new ItemStack(Material.valueOf(getOrbCon().get("crafting.middle.left").toString())));
                else if (i == 20)
                    contents.add(new ItemStack(Material.valueOf(getOrbCon().get("crafting.middle.middle").toString())));
                else if (i == 21)
                    contents.add(new ItemStack(Material.valueOf(getOrbCon().get("crafting.middle.right").toString())));
                else if (i == 28)
                    contents.add(new ItemStack(Material.valueOf(getOrbCon().get("crafting.bottom.left").toString())));
                else if (i == 29)
                    contents.add(new ItemStack(Material.valueOf(getOrbCon().get("crafting.bottom.middle").toString())));
                else if (i == 30)
                    contents.add(new ItemStack(Material.valueOf(getOrbCon().get("crafting.bottom.right").toString())));
                else if (i == 24) contents.add(new ItemStack(orb));
                else if (i > 44) contents.add(new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                else contents.add(blank);
            }
            custommenu.setContents(contents.toArray(new ItemStack[0]));
            p.openInventory(custommenu);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
        } catch (Exception exception) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Failed to load custom origin orb recipe!");
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Either delete the file, or add a valid material/path.");
            p.sendMessage(ChatColor.RED + "Failed to load custom origin orb recipe!");
            p.sendMessage(ChatColor.RED + "If you are an admin: Either delete the file, or add a valid material/path.");
        }
    }

    @EventHandler
    public void stopStealing(InventoryClickEvent e) {
        //prevents interactions with the /o recipe command
        if (e.getView().getTitle().equalsIgnoreCase("Orb Recipe")) {
            e.setCancelled(true);
        }
    }

}
