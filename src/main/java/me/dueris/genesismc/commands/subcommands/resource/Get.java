package me.dueris.genesismc.commands.subcommands.resource;

import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.factory.powers.Resource;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.dueris.genesismc.utils.text.BukkitColour.RED;

import java.util.ArrayList;

public class Get extends SubCommand {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "gets some";
    }

    @Override
    public String getSyntax() {
        return "/resource get <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.resource.get.noPlayer")).color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.resource.get.noPower")).color(TextColor.fromHexString(RED)));
            return;
        }
        ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
        if (players.size() == 0) return;
        for (Player p : players) {
            if (Resource.registeredBars.containsKey(p) && Resource.registeredBars.get(p).containsKey(args[2])) {
                sender.sendMessage("$1 has %value% $2"
                        .replace("$2", args[2])
                        .replace("$1", p.getName())
                        .replace("%value%", String.valueOf(Resource.registeredBars.get(p).get(args[2]).getLeft().getProgress())));
            } else {
                sender.sendMessage(ChatColor.RED + "Can't get value of $2 for $1; none is set"
                        .replace("$2", args[2])
                        .replace("$1", p.getName()));
            }
        }
    }
}
