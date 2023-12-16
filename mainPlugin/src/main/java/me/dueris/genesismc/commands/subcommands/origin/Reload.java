package me.dueris.genesismc.commands.subcommands.origin;

import me.dueris.genesismc.OriginDataContainer;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.factory.CraftApoli;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Reload extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "reloads/reparses origins";
    }

    @Override
    public String getSyntax() {
        return "/origin reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.RED + "This action is unsupported and could cause major issues with origins.");
        sender.sendMessage(ChatColor.RED + "Use this command with caution. This could result in data loss, errors, and possibly a crash of the server.");
        try {
            CraftApoli.unloadData();
            CraftApoli.loadOrigins();
            OriginDataContainer.unloadAllData();
            OriginDataContainer.loadData();
            sender.sendMessage(ChatColor.GREEN + "Origins reloaded successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while attempting to reload origins", e);
        }

    }
}
