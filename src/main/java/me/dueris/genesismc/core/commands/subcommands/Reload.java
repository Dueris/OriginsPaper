package me.dueris.genesismc.core.commands.subcommands;

import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.Bukkit;
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
    if (p.hasPermission("genesismc.origins.cmd.reload")) {
      final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("GenesisMC");
      GenesisDataFiles.reload();
    }
  }
}
