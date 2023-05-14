package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        if(args.length > 1){
            Player given = Bukkit.getPlayer(args[1]);
            OriginPlayer.removeOrigin(given);
        }else
        if(args.length == 1){
            OriginPlayer.removeOrigin(p);
        }
    }
}
