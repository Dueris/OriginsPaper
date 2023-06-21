package me.dueris.genesismc.core.commands;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.commands.subcommands.origin.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

import static me.dueris.genesismc.core.utils.BukkitColour.RED;
import static me.dueris.genesismc.core.utils.BukkitColour.YELLOW;

public class GenesisCommandManager implements CommandExecutor {

    //key = uuid of player
    //long = epoch time of when ran command
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public GenesisCommandManager() {
        subCommands.add(new Enchant());
        subCommands.add(new References());
        subCommands.add(new Recipe());
        subCommands.add(new Get());
        subCommands.add(new Gui());
        subCommands.add(new Has());
        subCommands.add(new Info());
        subCommands.add(new Set());
        subCommands.add(new Give());
        subCommands.add(new Bug());
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < getSubCommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {
                    getSubCommands().get(i).perform(sender, args);
                    //OriginCommandEvent event = new OriginCommandEvent(sender);
                    //getServer().getPluginManager().callEvent(event);
                }

            }

        }
        if (args.length == 0) {
            sender.sendMessage(Component.text("You did not provide any args. Here is a list of commands:").color(TextColor.fromHexString(RED)));
            sender.sendMessage(Component.text("-----------------------------------------").color(TextColor.fromHexString(YELLOW)));
            sender.sendMessage(Component.text("/origin get <player> <origin layer>"));
            sender.sendMessage(Component.text("/origin set <player> <origin layer>"));
            sender.sendMessage(Component.text("/origin enchant <player> <genesis enchantment> <amount>"));
            sender.sendMessage(Component.text("/origin gui <player>"));
            sender.sendMessage(Component.text("/origin get <player> <genesis item> <amount>"));
            sender.sendMessage(Component.text("/origin info"));
            sender.sendMessage(Component.text("/origin has <player> <origin layer>"));
            sender.sendMessage(Component.text("/origin recipe"));
            sender.sendMessage(Component.text("/origin references"));
            sender.sendMessage(Component.text("/origin bug"));
            sender.sendMessage(Component.text("/shulker open"));
            sender.sendMessage(Component.text("-----------------------------------------").color(TextColor.fromHexString(YELLOW)));
        }
        return true;
    }


    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }
}