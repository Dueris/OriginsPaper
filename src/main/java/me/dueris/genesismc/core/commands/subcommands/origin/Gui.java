package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.PlayerSelector;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.LayerContainer;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class Gui extends SubCommand {
    @Override
    public String getName() {
        return "gui";
    }

    @Override
    public String getDescription() {
        return "opens origin choosing screen";
    }

    @Override
    public String getSyntax() {
        return "/origin gui <player>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("genesismc.origins.cmd.gui")) return;
        if (args.length > 1) {
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
            if (players.size() == 0) return;
            for (Player p : players) {
                for (LayerContainer layer : CraftApoli.getLayers()) {
                    OriginPlayer.unassignPowers(p, layer);
                    OriginPlayer.setOrigin(p, layer, CraftApoli.nullOrigin());
                }
            }
        } else if (args.length == 1 && sender instanceof Player p) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                OriginPlayer.unassignPowers(p, layer);
                OriginPlayer.setOrigin(p, layer, CraftApoli.nullOrigin());
            }
        }
    }
}
