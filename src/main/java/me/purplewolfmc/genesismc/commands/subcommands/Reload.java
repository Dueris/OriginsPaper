package me.purplewolfmc.genesismc.commands.subcommands;

import me.purplewolfmc.genesismc.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Reload extends SubCommand implements Listener {

  @Override
  public String getName() {
    return "reload";
  }

  @Override
  public String getDescription() {
    return "reloads config file";
  }

  @Override
  public String getSyntax() {
    return "/origins reload";
  }

  @Override
  public void perform(Player p, String[] args) {
    final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("GenesisMC");
    GenesisDataFiles.reload();
  }
}
