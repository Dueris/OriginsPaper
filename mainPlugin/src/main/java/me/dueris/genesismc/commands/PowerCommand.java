package me.dueris.genesismc.commands;

import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.commands.subcommands.origin.*;
import me.dueris.genesismc.commands.subcommands.origin.Info.Info;
import me.dueris.genesismc.commands.subcommands.power.Clear;
import me.dueris.genesismc.commands.subcommands.power.Grant;
import me.dueris.genesismc.commands.subcommands.power.Has;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.Power;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PowerCommand implements CommandExecutor {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    public PowerCommand(){
        subCommands.add(new Clear());
        subCommands.add(new Grant());
        subCommands.add(new Has());
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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

    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }
}
