package me.purplewolfmc.genesismc.core.commands.beta;


import me.purplewolfmc.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.entity.Player;

import static me.purplewolfmc.genesismc.core.items.OrbOfOrigins.orb;

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
        p.getInventory().addItem(orb);
        p.sendMessage("test");
    }
}
