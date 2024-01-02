package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

public class Grant extends SubCommand {
    @Override
    public String getName() {
        return "grant";
    }

    @Override
    public String getDescription() {
        return "grants a power";
    }

    @Override
    public String getSyntax() {
        return "/power grant <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

    }
}
