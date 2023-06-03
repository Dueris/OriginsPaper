package me.dueris.genesismc.core.utils;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ShulkInv implements CommandExecutor {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public ShulkInv() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player p) {

            if (OriginPlayer.getOrigin(p).getTag().equalsIgnoreCase("genesis:origin-shulk")) {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("open")) {

                        ArrayList<ItemStack> vaultItems = ShulkUtils.getItems(p);

                        Inventory vault = Bukkit.createInventory(p, InventoryType.DROPPER, "Shulker Inventory");

                        vaultItems.stream()
                                .forEach(itemStack -> vault.addItem(itemStack));

                        p.openInventory(vault);

                    }
                } else if (args.length == 0) {
                    ArrayList<ItemStack> vaultItems = ShulkUtils.getItems(p);

                    Inventory vault = Bukkit.createInventory(p, InventoryType.DROPPER, "Shulker Inventory");

                    vaultItems.stream()
                            .forEach(itemStack -> vault.addItem(itemStack));

                    p.openInventory(vault);

                }
            } else {
                p.sendMessage(ChatColor.RED + "You must be a Shulk to access this command");
            }
        }


        return true;
    }
}

