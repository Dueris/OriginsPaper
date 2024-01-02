package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

public class List extends SubCommand {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "lists the powers the player has";
    }

    @Override
    public String getSyntax() {
        return "/power list <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

    }
}
