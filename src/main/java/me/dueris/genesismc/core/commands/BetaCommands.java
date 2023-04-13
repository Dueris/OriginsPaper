package me.dueris.genesismc.core.commands;

import me.dueris.genesismc.core.commands.beta.BetaOrbOrigins;
import me.dueris.genesismc.core.commands.beta.BetaWaterProt;
import me.dueris.genesismc.core.commands.beta.EnchantCMD;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BetaCommands implements CommandExecutor {

    //key = uuid of player
    //long = epoch time of when ran command
    private ArrayList<SubCommand> subCommands = new ArrayList<>();
    public BetaCommands(){
        subCommands.add(new BetaOrbOrigins());
        subCommands.add(new BetaWaterProt());
        subCommands.add(new EnchantCMD());
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