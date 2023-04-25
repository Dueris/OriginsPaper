package me.dueris.genesismc.core.commands;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static me.dueris.genesismc.core.origins.rabbit.RabbitLeap.*;

public class ToggleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (p.hasPermission("genesismc.origins.cmd.toggle")) {

                int toggleState = data.get(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER);
                if (toggleState == 1) data.set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, 2);
                else data.set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, 1);

                if (originid == 5308033) {
                    leapToggle(p);
                } else if (false) {
                    ; //add other origin toggles here like this
                } else {
                    p.sendMessage(ChatColor.RED + "Your origin does not have an ability that can be toggled");
                }

            } else {
                p.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3f, 1);
            }
        } else if (sender instanceof ConsoleCommandSender) {
            sender.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This is a player only command.");
        }
        return true;
    }
}
