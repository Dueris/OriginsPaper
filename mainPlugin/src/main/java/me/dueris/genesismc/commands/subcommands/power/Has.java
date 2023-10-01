package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Has extends SubCommand {
    @Override
    public String getName() {
        return "has";
    }

    @Override
    public String getDescription() {
        return "returns if has rewop";
    }

    @Override
    public String getSyntax() {
        return "/power has <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[2]);
            for (Player p : players) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    for (PowerContainer powerContainer : origin.getPowerContainers()) {

                    }
                }
            }
        }
    }
}
