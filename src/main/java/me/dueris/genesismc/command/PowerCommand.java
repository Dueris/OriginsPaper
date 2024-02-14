package me.dueris.genesismc.command;

import me.dueris.genesismc.command.subcommands.SubCommand;
import me.dueris.genesismc.command.subcommands.power.Dump;
import me.dueris.genesismc.command.subcommands.power.Grant;
import me.dueris.genesismc.command.subcommands.power.Has;
import me.dueris.genesismc.command.subcommands.power.Remove;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PowerCommand extends Command {
    private static final ArrayList<SubCommand> subCommands = new ArrayList<>();

    static {
        subCommands.add(new Remove());
        subCommands.add(new Has());
        subCommands.add(new me.dueris.genesismc.command.subcommands.power.List());
        subCommands.add(new Dump());
        subCommands.add(new Grant());
    }

    public PowerCommand() {
        super("power");
    }

    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        AutoComplete tabAutoComplete = new AutoComplete();
        return tabAutoComplete.onTabComplete(sender, this, alias, args);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.isOp()) return false;
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
