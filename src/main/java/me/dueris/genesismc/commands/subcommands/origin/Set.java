package me.dueris.genesismc.commands.subcommands.origin;

import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.enums.OriginDataType;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.utils.BukkitColour.RED;
import static org.bukkit.Bukkit.getServer;

public class Set extends SubCommand {
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "command.origin.set.description");
    }

    @Override
    public String getSyntax() {
        return "/origin set <player> <origin>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("genesismc.origins.cmd.set")) return;
        if (args.length == 1) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.origin.set.noPlayer")).color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.origin.set.noLayer")).color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length == 3) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender,"command.origin.set.noOrigin")).color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length > 3) {
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
            if (players.size() == 0) return;

            if (!CraftApoli.getLayers().contains(CraftApoli.getLayerFromTag(args[2]))) {
                sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender,"command.origin.set.invalidLayer")).color(TextColor.fromHexString(RED)));
                return;
            }

            String originTag = args[3];
            if (!CraftApoli.getOriginTags().contains(originTag)) {
                sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender,"command.origin.set.invalidOrigin")).color(TextColor.fromHexString(RED)));
                return;
            }

            for (Player p : players) {
                OriginPlayer.setOrigin(p, CraftApoli.getLayerFromTag(args[2]), CraftApoli.getOrigin(originTag));
                OriginPlayer.resetOriginData(p, OriginDataType.IN_PHASING_FORM);
                OriginChangeEvent originChangeEvent = new OriginChangeEvent(p);
                getServer().getPluginManager().callEvent(originChangeEvent);
            }
        }
    }
}
