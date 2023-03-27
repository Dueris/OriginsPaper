package me.purplewolfmc.genesismc.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandListCMD extends SubCommand{
    @Override
    public String getName() {
        return "commands";
    }

    @Override
    public String getDescription() {
        return "lists all commands in list";
    }

    @Override
    public String getSyntax() {
        return "/origins commands";
    }

    @Override
    public void perform(Player p, String[] args) {
        p.sendMessage(ChatColor.YELLOW + "-----------------------------------------",
                ChatColor.WHITE + "/origins choose",
                ChatColor.WHITE + "/origins purge <player_name>",
                ChatColor.WHITE + "/origins reload",
                ChatColor.WHITE + "/origins commands",
                ChatColor.WHITE + "/origins config",
                ChatColor.WHITE + "/origins texture",
                ChatColor.WHITE + "/shulker open",
                ChatColor.WHITE + "/beta <beta-args>",
                ChatColor.YELLOW + "-----------------------------------------"
        );
    }
}
