package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandList extends SubCommand {
    @Override
    public String getName() {
        return "commands";
    }

    @Override
    public String getDescription() {
        return "lists all commands in list";
    }

    @Override
    public String getSyntax() {
        return "/origin commands";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (p.hasPermission("genesismc.origins.cmd.commandlist")) {
            p.sendMessage(ChatColor.YELLOW + "-----------------------------------------",
                    ChatColor.WHITE + "/origin choose",
                    ChatColor.WHITE + "/origin purge <player_name>",
                    ChatColor.WHITE + "/origin reload",
                    ChatColor.WHITE + "/origin commands",
                    ChatColor.WHITE + "/origin config",
                    ChatColor.WHITE + "/origin texture",
                    ChatColor.WHITE + "/origin get <player_name>",
                    ChatColor.WHITE + "/origin enchant <enchantment-id> <level>",
                    ChatColor.WHITE + "/shulker open",
                    ChatColor.WHITE + "/leap or /l",
                    ChatColor.WHITE + "/beta <beta-args>",
                    ChatColor.YELLOW + "-----------------------------------------",
                    System.out.printf(args[0]).toString()
            );
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
