package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.commands.PlayerSelector;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.core.utils.BukkitColour.RED;

public class Has extends SubCommand {
    @Override
    public String getName() {
        return "has";
    }

    @Override
    public String getDescription() {
        return "test to check if player has origin";
    }

    @Override
    public String getSyntax() {
        return "/origin has <player> <layer> <origintag>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("genesismc.origins.cmd.has")) return;
        if (args.length == 1) {
            sender.sendMessage(Component.text("No player specified!").color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(Component.text("No layer specified!").color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length == 3) {
            sender.sendMessage(Component.text("No origin specified!").color(TextColor.fromHexString(RED)));
            return;
        }
        ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
        if (players.size() == 0) return;
        if (!CraftApoli.getLayers().contains(args[2])) {
            sender.sendMessage(Component.text("Invalid layer!").color(TextColor.fromHexString(RED)));
            return;
        }
        String originTag = args[3];
        if (!CraftApoli.getOriginTags().contains(originTag)) {
            sender.sendMessage(Component.text("Invalid origin!").color(TextColor.fromHexString(RED)));
            return;
        }

        for (Player p : players) {
            HashMap<String, OriginContainer> origins = CraftApoli.toOriginContainer(p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY));
            assert origins != null;
            for (String layer : origins.keySet()) {
                if (!layer.equals(args[2])) continue;
                if (OriginPlayer.hasOrigin(p, args[3])) sender.sendMessage(Component.text(p.getName() + " Passed the test!"));
                else sender.sendMessage(Component.text(p.getName() + " Failed the test."));
            }
        }
    }
}
