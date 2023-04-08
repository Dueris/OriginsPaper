package me.purplewolfmc.genesismc.core.commands;

import me.purplewolfmc.genesismc.core.commands.subcommands.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GenesisCommandManager implements CommandExecutor {

    //key = uuid of player
    //long = epoch time of when ran command
    private ArrayList<SubCommand> subCommands = new ArrayList<>();
    public GenesisCommandManager(){
        subCommands.add(new GenesisChoosing());
        subCommands.add(new Purge());
        subCommands.add(new Reload());
        subCommands.add(new Texture());
        subCommands.add(new CommandListCMD());
        subCommands.add(new ConfigCMD());
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
        }
        return true;
    }







public ArrayList<SubCommand> getSubCommands(){
        return subCommands;
}
}