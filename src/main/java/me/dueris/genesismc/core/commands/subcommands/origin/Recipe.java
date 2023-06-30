package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
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

import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemProperties;
import static me.dueris.genesismc.core.files.GenesisDataFiles.getMainConfig;
import static me.dueris.genesismc.core.files.GenesisDataFiles.getOrbCon;
import static me.dueris.genesismc.core.utils.BukkitColour.RED;

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
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player p) {
            if (!p.hasPermission("genesismc.origins.cmd.recipe")) return;
            if (getMainConfig().get("orb-of-origins") == null || getOrbCon().get("orb-of-origins-enabled").toString() != "true") {
                p.sendMessage(Component.text("Orb of origin crafting has been disabled by a server admin!").color(TextColor.fromHexString(RED)));
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
                    else if (i == 49)
                        contents.add(itemProperties(new ItemStack(Material.BARRIER), ChatColor.RED + "Close", null, null, ChatColor.RED + "Close recipe menu."));
                    else if (i > 44) contents.add(new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                    else contents.add(blank);
                }
                custommenu.setContents(contents.toArray(new ItemStack[0]));
                p.openInventory(custommenu);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
            } catch (Exception exception) {
                sender.sendMessage(Component.text("Failed to load custom origin orb recipe!").color(TextColor.fromHexString(RED)));
                sender.sendMessage(Component.text("If you are an admin: Either delete the file, or add a valid material/path.").color(TextColor.fromHexString(RED)));
            }
        } else
            sender.sendMessage(Component.text("You must be a player to use this command!").color(TextColor.fromHexString(RED)));
    }

    @EventHandler
    public void stopStealingRecipe(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("Orb Recipe")) e.setCancelled(true);
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
