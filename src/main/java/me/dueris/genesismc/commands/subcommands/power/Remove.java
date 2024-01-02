package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

public class Remove extends SubCommand {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "removes a power";
    }

    @Override
    public String getSyntax() {
        return "/power remove <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

    }
}
