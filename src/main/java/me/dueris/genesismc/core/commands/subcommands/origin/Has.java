package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.PlayerSelector;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.entity.OriginPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.core.utils.Colours.RED;

public class Has extends SubCommand {
    @Override
    public String getName() {
        return "has";
    }

    @Override
    public String getDescription() {
        return "test to check if player has origin";
    }

    @Override
    public String getSyntax() {
        return "/origin has <player> <layer> <origintag>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("genesismc.origins.cmd.has")) return;
        if (args.length == 1) {
            sender.sendMessage(Component.text("No player specified!").color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(Component.text("No origin specified!").color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length > 2) {
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
            if (players.size() == 0) return;
            for (Player p : players) {
                if (OriginPlayer.getOrigin(p).getTag().equalsIgnoreCase(args[2])) sender.sendMessage(Component.text(p.getName()+" Passed the test!"));
                else sender.sendMessage(Component.text(p.getName()+" Failed the test."));
            }
        }
    }
}
