package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.PlayerSelector;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.OriginChangeEvent;
import me.dueris.genesismc.core.factory.CraftApoli;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.core.utils.Colours.RED;
import static org.bukkit.Bukkit.getServer;

public class Set extends SubCommand {
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "sets origin of given player";
    }

    @Override
    public String getSyntax() {
        return "/origin set <player> <origin>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("genesismc.origins.cmd.set")) return;
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

            String originTag = args[2];
            if (!CraftApoli.getOriginTags().contains(originTag)) {
                sender.sendMessage(Component.text("Invalid origin!").color(TextColor.fromHexString(RED)));
                return;
            }

            for (Player p : players) {
                OriginPlayer.setOrigin(p, CraftApoli.getOrigin(originTag));
                OriginChangeEvent originChangeEvent = new OriginChangeEvent(p);
                getServer().getPluginManager().callEvent(originChangeEvent);
            }
        } else {
        }
    }
}
