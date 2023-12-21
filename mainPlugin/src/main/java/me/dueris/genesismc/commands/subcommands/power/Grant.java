package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Grant extends SubCommand {
    @Override
    public String getName() {
        return "grant";
    }

    @Override
    public String getDescription() {
        return "grants a power i think?";
    }

    @Override
    public String getSyntax() {
        return "/power grant <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[2]);
            for (Player p : players) {
                for (PowerContainer c : CraftApoli.getPowers()) {
                    if (c.getTag().equalsIgnoreCase(args[3])) {
                        for (Class<? extends CraftPower> cX : CraftPower.getRegistered()) {
                            try {
                                if (cX.newInstance().getPowerFile().equalsIgnoreCase(c.getType())) {
                                    OriginPlayerUtils.getPowersApplied(p).add(cX);
                                    sender.sendMessage("Entity " + p.getName() + " was granted the power " + c.getName() + " from source apoli:command");
                                    break;
                                }
                            } catch (InstantiationException e) {
                                throw new RuntimeException(e);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }
}
