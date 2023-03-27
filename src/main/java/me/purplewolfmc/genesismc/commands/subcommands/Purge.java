package me.purplewolfmc.genesismc.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Purge extends SubCommand implements Listener {
    @Override
    public String getName() {
        return "purge";
    }

    @Override
    public String getDescription() {
        return "removes player origin";
    }

    @Override
    public String getSyntax() {
        return "/origins purge <player>";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (p.hasPermission("genesismc.origins.purge")) {
            if (args.length > 1) {
                Player target = Bukkit.getPlayer(args[1]);

                p.sendMessage("[GenesisMC] Removed origin of " + target.getDisplayName());
                target.getScoreboardTags().remove("chosen");
                target.getScoreboardTags().remove("enderian");
                target.getScoreboardTags().remove("shulker");
                target.getScoreboardTags().remove("arachnid");
                target.getScoreboardTags().add("human");
                target.getScoreboardTags().remove("phantom");
                target.getScoreboardTags().remove("slimeling");
                target.getScoreboardTags().remove("vexian");
                target.getScoreboardTags().remove("blazeborn");
                target.getScoreboardTags().remove("starborne");
                target.getScoreboardTags().remove("mermaid");
                target.getScoreboardTags().remove("witch");
                target.getScoreboardTags().remove("rabbit");
                target.getScoreboardTags().remove("bee");
                target.getScoreboardTags().remove("elytrian");
                target.getScoreboardTags().remove("avian");
                target.getScoreboardTags().remove("piglin");
                target.getScoreboardTags().remove("dragonborne");
                target.getScoreboardTags().remove("creep");

                target.sendMessage("Your origin has been removed by an operator");
            }
        }
    }
}
