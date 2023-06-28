package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.PlayerSelector;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.core.utils.BukkitColour.RED;

public class Get extends SubCommand {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "gets origin of player";
    }

    @Override
    public String getSyntax() {
        return "/origin get <player>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("genesismc.origins.cmd.get")) return;
        if (args.length == 1) {
            sender.sendMessage(Component.text("No player specified!").color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(Component.text("No layer specified!").color(TextColor.fromHexString(RED)));
            return;
        }

        ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
        if (players.size() == 0) return;
        for (Player p : players)
            sender.sendMessage(Component.text(p.getName() + " has the following " + args[2] + " : " + OriginPlayer.getOrigin(p, CraftApoli.getLayerFromTag(args[2])).getTag()));

    }
}
