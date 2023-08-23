package me.dueris.genesismc.commands.subcommands;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract void perform(CommandSender sender, String[] args);

}
