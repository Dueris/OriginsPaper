package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import me.dueris.genesismc.core.api.factory.CustomOriginAPI;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.RED;

public class Get extends SubCommand {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "gets origin of player";
    }

    @Override
    public String getSyntax() {
        return "/origin get <player>";
    }

    @Override
    public void perform(Player p, String[] args) {
        if(args.length > 1){
            try {
            Player given = Bukkit.getPlayer(args[1]);
            p.sendMessage(given.getName() + " has the following Origin: " + OriginPlayer.getOriginTag(given));
            } catch (Exception e) {
                p.sendMessage(RED + "Could not find player!");
            }
        }else
        if(args.length == 1){
            p.sendMessage(p.getName() + " has the following Origin: " + OriginPlayer.getOriginTag(p));
        }

    }
}
