package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
        if(args.length > 2){
            Player given = Bukkit.getPlayer(args[1]);
            String origintag = args[2];

            OriginPlayer.setOrigin(p, origintag);

        }else{
            p.sendMessage(ChatColor.RED + "Invalid Args!!!");
        }
    }
}
