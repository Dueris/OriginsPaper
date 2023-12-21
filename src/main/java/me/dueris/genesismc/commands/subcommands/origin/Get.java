package me.dueris.genesismc.commands.subcommands.origin;

import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.utils.BukkitColour.RED;

public class Get extends SubCommand {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "command.origin.get.description");
    }

    @Override
    public String getSyntax() {
        return "/origin get <player>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("genesismc.origins.cmd.get")) return;
        if (args.length == 1) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.origin.get.noPlayer")).color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.origin.get.noLayer")).color(TextColor.fromHexString(RED)));
            return;
        }

        ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
        if (players.size() == 0) return;
        for (Player p : players)
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(p, "command.origin.get.output").replace("%player%", p.getName()).replace("%layer%", args[2]).replace("%origin%", OriginPlayerUtils.getOrigin(p, CraftApoli.getLayerFromTag(args[2])).getTag())));
    }
}
