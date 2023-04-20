package me.dueris.genesismc.core.commands.subcommands.beta;


import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.entity.Player;

import java.util.List;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;

public class BetaOrbOrigins extends SubCommand {
    @Override
    public String getName() {
        return "orboforigin";
    }

    @Override
    public String getDescription() {
        return "spawns the orb of origin";
    }

    @Override
    public String getSyntax() {
        return "/beta orboforigin";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (p.hasPermission("genesismc.origins.cmd.beta") && p.hasPermission("genesismc.origins.beta.orboforigin")) {
            p.getInventory().addItem(orb);
            p.sendMessage("test");
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
