package me.dueris.genesismc.commands;

import me.dueris.genesismc.commands.subcommands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
public class PowerCommand extends Command{
    public PowerCommand(){
        super("power");
    }

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        TabAutoComplete tabAutoComplete = new TabAutoComplete();
        return tabAutoComplete.onTabComplete(sender, this, alias, args);
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
