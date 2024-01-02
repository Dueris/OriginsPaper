package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

public class Clear extends SubCommand {
    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "clears a power";
    }

    @Override
    public String getSyntax() {
        return "/power clear <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

    }
}
