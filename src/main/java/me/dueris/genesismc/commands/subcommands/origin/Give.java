package me.dueris.genesismc.commands.subcommands.origin;

import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.utils.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.dueris.genesismc.items.OrbOfOrigins.orb;
import static me.dueris.genesismc.utils.BukkitColour.RED;

public class Give extends SubCommand {
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return Lang.getLocalizedString("command.origin.give.description");
    }

    @Override
    public String getSyntax() {
        return "/origin give <player> <item> <amount>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("genesismc.origins.cmd.give")) return;
        if (args.length == 1) {
            sender.sendMessage(Component.text(Lang.getLocalizedString("command.origin.give.noPlayer")).color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(Component.text(Lang.getLocalizedString("command.origin.give.noLayer")).color(TextColor.fromHexString(RED)));
            return;
        }

        ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);

        if (players.size() == 0) return;

        ItemStack item;
        if (args[2].equals("genesis:orb_of_origin")) {
            item = orb.clone();
        } else return;

        if (args.length == 4) {
            try {
                item.setAmount(Integer.parseInt(args[3].strip()));
            } catch (Exception e) {
                sender.sendMessage(Component.text(Lang.getLocalizedString("command.origin.give.wrongNumber")).color(TextColor.fromHexString(RED)));
                return;
            }
        } else {
            item.setAmount(1);
        }

        for (Player player : players) {
            if (args[2].equals("genesis:orb_of_origin")) player.getInventory().addItem(item);
        }
    }
}
