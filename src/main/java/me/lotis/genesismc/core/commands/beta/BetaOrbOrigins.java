package me.lotis.genesismc.core.commands.beta;


import me.lotis.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.entity.Player;

import static me.lotis.genesismc.core.items.OrbOfOrigins.orb;

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
}
