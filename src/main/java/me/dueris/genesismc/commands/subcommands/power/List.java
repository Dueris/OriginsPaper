package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
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
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Please provide a player arg.");
        } else if (args.length >= 1) {
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
            for (Player p : players) {
                for (LayerContainer layerContainer : CraftApoli.getLayers()) {
                    java.util.List<PowerContainer> powers = OriginPlayerUtils.playerPowerMapping.get(p).get(layerContainer);
                    if(powers == null || powers.isEmpty()){
                        sender.sendMessage(ChatColor.RED + "Entity %name% does not have any powers".replace("%name%", p.getName()));
                    }else{
                        String msg = "Entity %name% has %size% powers: [%powers%]".replace("%name%", p.getName()).replace("%size%", String.valueOf(powers.size()));
                        final String[] powerString = {""};
                        powers.forEach((power) -> {
                            powerString[0] = powerString[0] + power.getTag() + ", ";
                        });
                        String finMsg = msg.replace("%powers%", powerString[0]);
                        sender.sendMessage(finMsg.replace(", ]", "]"));
                    }
                }
            }
        }
    }
}
