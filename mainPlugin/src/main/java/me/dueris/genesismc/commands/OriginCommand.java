package me.dueris.genesismc.commands;

import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.commands.subcommands.origin.*;
import me.dueris.genesismc.commands.subcommands.origin.Info.Info;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

import static me.dueris.genesismc.utils.BukkitColour.RED;
import static me.dueris.genesismc.utils.BukkitColour.YELLOW;

public class OriginCommand implements CommandExecutor {

    //key = uuid of player
    //long = epoch time of when ran command
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public OriginCommand() {
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
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.origin.empty")).color(TextColor.fromHexString(RED)));
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