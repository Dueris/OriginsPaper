package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.RED;

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
    public void perform(Player p, String[] args) {
        if (!p.hasPermission("genesismc.origins.cmd.gui")) return;
        if (args.length > 1) {
            try {
                Player given = Bukkit.getPlayer(args[1]);
                OriginPlayer.unassignPowers(given);
                OriginPlayer.removeOrigin(given);
            } catch (Exception e) {
                p.sendMessage(RED + "Could not find player!");
            }
        } else if (args.length == 1) {
            OriginPlayer.unassignPowers(p);
            OriginPlayer.removeOrigin(p);
        }
    }
}
