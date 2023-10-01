package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Clear extends SubCommand {
    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "clears stuff";
    }

    @Override
    public String getSyntax() {
        return "/power clear <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[2]);
            for (Player p : players) {
                int r = 0;
                for (Class<? extends CraftPower> c : OriginPlayer.getPowersApplied(p)) {
                    OriginPlayer.getPowersApplied(p).remove(c);
                    r++;
                }
                p.sendMessage("Entity " + p.getName() + " had " + r + " powers cleared");
            }
        }
    }
}
