package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

public class Has extends SubCommand {
    @Override
    public String getName() {
        return "has";
    }

    @Override
    public String getDescription() {
        return "returns if the player has that power";
    }

    @Override
    public String getSyntax() {
        return "/power has <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

    }
}
