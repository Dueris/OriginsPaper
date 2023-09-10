package me.dueris.genesismc.commands;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PowerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            if(args.length >= 1){
                if(args[1].equalsIgnoreCase("clear")){
                    if(args.length >= 2){
                        ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[2]);
                        for(Player p : players){
                            int r = 0;
                            for(Class<? extends CraftPower> c : OriginPlayer.getPowersApplied(p)){
                                OriginPlayer.getPowersApplied(p).remove(c);
                                r++;
                            }
                            p.sendMessage("Entity " + p.getName() + " had " + r + " powers cleared");
                        }
                    }
                } else if (args[1].equalsIgnoreCase("grant")) {
                    if(args.length >= 2) {
                            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[2]);
                            for(Player p : players){
                            for(PowerContainer c : CraftApoli.getPowers()){
                                if(c.getTag().equalsIgnoreCase(args[3])){
                                    for(Class<? extends CraftPower> cX : CraftPower.getRegistered()){
                                        try {
                                            if(cX.newInstance().getPowerFile().equalsIgnoreCase(c.getType())) {
                                                OriginPlayer.getPowersApplied(p).add(cX);
                                                sender.sendMessage("Entity " + p.getName() + " was granted the power " + c.getName() + " from source apoli:command");
                                                break;
                                            }
                                        } catch (InstantiationException e) {
                                            throw new RuntimeException(e);
                                        } catch (IllegalAccessException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (args[1].equalsIgnoreCase("has")) {
                    if(args.length >= 2) {
                        ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[2]);
                        for(Player p : players){
                            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                                for(PowerContainer powerContainer : origin.getPowerContainers()){

                                }
                            }
                        }
                        }
                } else if (args[1].equalsIgnoreCase("remove")) {

                } else if (args[1].equalsIgnoreCase("revoke")) {

                } else if (args[1].equalsIgnoreCase("revokeall")) {

                }
            }
        }
        return true;
    }
}
