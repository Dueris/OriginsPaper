package me.dueris.genesismc.command;

import me.dueris.genesismc.command.subcommands.SubCommand;
import me.dueris.genesismc.command.subcommands.resource.Change;
import me.dueris.genesismc.command.subcommands.resource.Has;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ResourceCommand extends Command {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public ResourceCommand() {
        super("resource");
        subCommands.add(new Change());
        subCommands.add(new Has());
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        AutoComplete tabAutoComplete = new AutoComplete();
        return tabAutoComplete.onTabComplete(sender, this, alias, args);
    }

    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < getSubCommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {
                    getSubCommands().get(i).perform(sender, args);
                    //OriginCommandEvent event = new OriginCommandEvent(sender);
                    //getServer().getPluginManager().callEvent(event);
                }

            }

        }
        return true;
    }
}
