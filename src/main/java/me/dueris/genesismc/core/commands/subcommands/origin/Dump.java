package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class Dump extends SubCommand {
    @Override
    public String getName() {
        return "dump";
    }

    @Override
    public String getDescription() {
        return "dumps config data into console";
    }

    @Override
    public String getSyntax() {
        return "/origins dump";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (p.hasPermission("genesismc.origins.cmd.config.dump")) {
            GenesisMC.dumpCon();
            p.sendMessage("Config info dumped into console.");
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
