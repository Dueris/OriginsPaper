package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Dump extends SubCommand {
    @Override
    public String getName() {
        return "dump";
    }

    @Override
    public String getDescription() {
        return "dumps the json data of a specified power";
    }

    @Override
    public String getSyntax() {
        return "/power dump <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Please provide a power arg.");
        } else if (args.length >= 1) {
            PowerContainer power = CraftApoli.keyedPowerContainers.get(args[1]);
            sender.sendMessage(power.getJsonData());
        }
    }
}
