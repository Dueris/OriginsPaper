package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.OriginChangeEvent;
import me.dueris.genesismc.core.factory.CraftApoliRewriten;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.RED;

public class Set extends SubCommand {
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "sets origin of given player";
    }

    @Override
    public String getSyntax() {
        return "/origin set <player> <origin>";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (!p.hasPermission("genesismc.origins.cmd.set")) return;
        if (args.length > 2) {
            Player given = Bukkit.getPlayer(args[1]);
            if (given == null) {
                p.sendMessage(RED + "Not a valid player.");
                return;
            }

            String originTag = args[2];
            if (!CraftApoliRewriten.getOriginTags().contains(originTag)) {
                p.sendMessage(RED + "Invalid origin.");
                return;
            }

            OriginPlayer.setOrigin(given, CraftApoliRewriten.getOrigin(originTag));
            OriginChangeEvent originChangeEvent = new OriginChangeEvent(given);
            getServer().getPluginManager().callEvent(originChangeEvent);
        } else {
            p.sendMessage(RED + "Invalid Args!!!");
        }
    }
}
