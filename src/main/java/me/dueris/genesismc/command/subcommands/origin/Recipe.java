package me.dueris.genesismc.command.subcommands.origin;

import me.dueris.genesismc.command.subcommands.SubCommand;
import me.dueris.genesismc.content.OrbOfOrigins;
import me.dueris.genesismc.util.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.dueris.genesismc.storage.GenesisDataFiles.getMainConfig;
import static me.dueris.genesismc.storage.GenesisDataFiles.getOrbCon;
import static me.dueris.genesismc.util.BukkitColour.RED;

public class Recipe extends SubCommand implements Listener {
    @Override
    public String getName() {
        return "Recipe";
    }

    @Override
    public String getDescription() {
        return LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "command.origin.recipe.description");
    }

    @Override
    public String getSyntax() {
        return "/origin recipe";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player p) {
            if (!p.hasPermission("genesismc.origins.cmd.recipe") || !getMainConfig().getBoolean("orb-of-origins")) return;
            @NotNull CraftInventoryCustom custommenu = (CraftInventoryCustom) Bukkit.createInventory(p, InventoryType.WORKBENCH, "Orb of Origins");

            try {
                custommenu.setItem(1, new ItemStack(Material.valueOf(getOrbCon().get("crafting.top.left").toString())));
                custommenu.setItem(2, new ItemStack(Material.valueOf(getOrbCon().get("crafting.top.middle").toString())));
                custommenu.setItem(3, new ItemStack(Material.valueOf(getOrbCon().get("crafting.top.right").toString())));
                custommenu.setItem(4, new ItemStack(Material.valueOf(getOrbCon().get("crafting.middle.left").toString())));
                custommenu.setItem(5, new ItemStack(Material.valueOf(getOrbCon().get("crafting.middle.middle").toString())));
                custommenu.setItem(6, new ItemStack(Material.valueOf(getOrbCon().get("crafting.middle.right").toString())));
                custommenu.setItem(7, new ItemStack(Material.valueOf(getOrbCon().get("crafting.bottom.left").toString())));
                custommenu.setItem(8, new ItemStack(Material.valueOf(getOrbCon().get("crafting.bottom.middle").toString())));
                custommenu.setItem(9, new ItemStack(Material.valueOf(getOrbCon().get("crafting.bottom.right").toString())));
                custommenu.setItem(0, OrbOfOrigins.orb);
                p.openInventory(custommenu);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
            } catch (Exception exception) {
                sender.sendMessage(Component.text(LangConfig.getLocalizedString(p, "command.origin.recipe.error")).color(TextColor.fromHexString(RED)));
            }
        } else
            sender.sendMessage(Component.text("This command can only be executed by the Player").color(TextColor.fromHexString(RED)));
    }

    @EventHandler
    public void stopStealingRecipe(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("Orb of Origins") && e.getView().getTopInventory().getType().equals(InventoryType.WORKBENCH)) e.setCancelled(true);
    }

    @EventHandler
    public void onMenuExitRecipe(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getView().getTitle().equalsIgnoreCase("Orb Recipe")) {
            if (e.getCurrentItem().getType() == Material.BARRIER) {
                Player p = (Player) e.getWhoClicked();
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                e.getWhoClicked().getInventory().close();
            }
        }
    }

}
