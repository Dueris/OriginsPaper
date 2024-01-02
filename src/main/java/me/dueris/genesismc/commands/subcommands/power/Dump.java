package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

public class Dump extends SubCommand {
    @Override
    public String getName() {
        return "dump";
    }

    @Override
    public String getDescription() {
        return "dumps the json data of a specified power";
    }

    @Override
    public String getSyntax() {
        return "/power dump <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

    }
}
