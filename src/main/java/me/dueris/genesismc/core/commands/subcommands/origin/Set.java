package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import me.dueris.genesismc.core.api.factory.CustomOriginAPI;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
        if(args.length > 2){
            Player given = Bukkit.getPlayer(args[1]);
            if (given== null) {
                p.sendMessage(RED +"Not a valid player.");
                return;
            }

            String originTag = args[2];
            ArrayList<String> originTags = new ArrayList<>(List.of("genesis:origin-human","genesis:origin-enderian","genesis:origin-merling","genesis:origin-phantom","genesis:origin-elytrian","genesis:origin-blazeborn","genesis:origin-avian","genesis:origin-arachnid","genesis:origin-shulk","genesis:origin-feline","genesis:origin-starborne","genesis:origin-allay","genesis:origin-rabbit","genesis:origin-bee","genesis:origin-sculkling","genesis:origin-creep","genesis:origin-slimeling","genesis:origin-piglin"));
            originTags.addAll(CustomOriginAPI.getCustomOriginTags());
            if (!originTags.contains(originTag)) {
                p.sendMessage(RED + "Invalid origin.");
                return;
            }

            OriginPlayer.setOrigin(given, originTag);
        }else{
            p.sendMessage(RED + "Invalid Args!!!");
        }
    }
}
