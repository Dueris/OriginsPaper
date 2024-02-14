package me.dueris.genesismc.command;

import me.dueris.genesismc.command.subcommands.SubCommand;
import me.dueris.genesismc.command.subcommands.origin.*;
import me.dueris.genesismc.command.subcommands.origin.Info.Info;
import me.dueris.genesismc.util.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.util.BukkitColour.RED;
import static me.dueris.genesismc.util.BukkitColour.YELLOW;

public class OriginCommand extends Command {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public OriginCommand() {
        super("origin");
        subCommands.add(new Enchant());
        subCommands.add(new Recipe());
        subCommands.add(new Get());
        subCommands.add(new Gui());
        subCommands.add(new Has());
        subCommands.add(new Info());
        subCommands.add(new Set());
        subCommands.add(new Give());
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        AutoComplete tabAutoComplete = new AutoComplete();
        return tabAutoComplete.onTabComplete(sender, this, alias, args);
    }

    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
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
            sender.sendMessage(Component.text("/origin reload"));
            sender.sendMessage(Component.text("-----------------------------------------").color(TextColor.fromHexString(YELLOW)));
        }
        return true;
    }
}