package me.dueris.genesismc.commands.subcommands.resource;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.factory.powers.Resource;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.actions.Actions.resourceChangeTimeout;
import static me.dueris.genesismc.utils.BukkitColour.RED;

public class Change extends SubCommand {
    @Override
    public String getName() {
        return "change";
    }

    @Override
    public String getDescription() {
        return "afl;SJdkljgafd";
    }

    @Override
    public String getSyntax() {
        return "/resource change <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.resource.has.noPlayer")).color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.resource.has.noPower")).color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length == 3) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.resource.has.noValue")).color(TextColor.fromHexString(RED)));
            return;
        }
        ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
        if (players.size() == 0) return;
        for (Player p : players) {
            if (resourceChangeTimeout.containsKey(p)) return;
            String resource = args[2];
            int change = Integer.parseInt(args[3]);
            double finalChange = 1.0 / Resource.getResource(resource).getRight();
            BossBar bossBar = Resource.getResource(resource).getLeft();
            double toRemove = finalChange * change;
            double newP = bossBar.getProgress() + toRemove;
            if (newP > 1.0) {
                newP = 1.0;
            } else if (newP < 0) {
                newP = 0.0;
            }
            bossBar.setProgress(newP);
            bossBar.addPlayer(p);
            bossBar.setVisible(true);
            resourceChangeTimeout.put(p, true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    resourceChangeTimeout.remove(p);
                }
            }.runTaskLater(GenesisMC.getPlugin(), 2);
        }
    }
}
