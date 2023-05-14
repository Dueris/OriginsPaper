package me.dueris.genesismc.core.commands;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.commands.subcommands.origin.*;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GenesisCommandManager implements CommandExecutor {

    //key = uuid of player
    //long = epoch time of when ran command
    private ArrayList<SubCommand> subCommands = new ArrayList<>();
    public GenesisCommandManager(){
        subCommands.add(new Purge());
        subCommands.add(new OriginsChoose());
        subCommands.add(new Enchant());
        subCommands.add(new References());
        subCommands.add(new Recipe());
        subCommands.add(new Get());
        subCommands.add(new Gui());
        subCommands.add(new Has());
        subCommands.add(new Info());
        subCommands.add(new Set());
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player p = (Player) sender;
            if (args.length > 0){
                for (int i = 0; i < getSubCommands().size(); i++){
                    if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                        getSubCommands().get(i).perform(p,args);
                    }

                }
            }
            if(args.length == 0){
                p.sendMessage(ChatColor.RED + "You did not provide any args. Here is a list of commands:");
                p.sendMessage(ChatColor.YELLOW + "-----------------------------------------",
                        ChatColor.WHITE + "/origin choose",
                        ChatColor.WHITE + "/origin purge <player_name>",
                        ChatColor.WHITE + "/origin get <player>",
                        ChatColor.WHITE + "/origin set <player>",
                        ChatColor.WHITE + "/origin enchant",
                        ChatColor.WHITE + "/origin gui <player>",
                        ChatColor.WHITE + "/origin info",
                        ChatColor.WHITE + "/origin has <player>",
                        ChatColor.WHITE + "/origin recipe",
                        ChatColor.WHITE + "/origin references",
                        ChatColor.WHITE + "/shulker open",
                        ChatColor.YELLOW + "-----------------------------------------"
                );
            }
        }
        return true;
    }







public ArrayList<SubCommand> getSubCommands(){
        return subCommands;
}
}