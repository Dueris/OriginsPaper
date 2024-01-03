package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.Power;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class List extends SubCommand {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "lists the powers the player has";
    }

    @Override
    public String getSyntax() {
        return "/power list <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(args.length == 0){
            sender.sendMessage(ChatColor.RED + "Please provide a player arg.");
        } else if (args.length >= 1) {
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
            for(Player p : players) {
                for(LayerContainer layerContainer : CraftApoli.getLayers()){
                    ArrayList<PowerContainer> powers = OriginPlayerUtils.powerContainer.get(p).get(layerContainer);
                    for(PowerContainer power : powers){
                        if(power == null) return;
                        powers.addAll(CraftApoli.getNestedPowers(power));
                    }
                    if(powers == null || powers.isEmpty()){
                        sender.sendMessage(ChatColor.RED + "Entity %name% does not have any powers".replace("%name%", p.getName()));
                    }else{
                        PowerContainer[] powerContainers = powers.toArray(new PowerContainer[0]);
                        String msg = "Entity %name% has %size% powers: [%powers%]".replace("%name%", p.getName()).replace("%size%", String.valueOf(powerContainers.length));
                        String powerString = "";
                        for(int i = 0; i < powerContainers.length; i++){
                            String sep;
                            if(i == powerContainers.length){
                                sep = "";
                            }else{
                                sep = ", ";
                            }
                            powerString = powerString + powerContainers[i].getTag() + sep;
                        }
                        sender.sendMessage(msg.replace("%powers%", powerString));
                    }
                }
            }
        }
    }
}
