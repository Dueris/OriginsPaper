package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class Config extends SubCommand {
    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getDescription() {
        return "dumps config data into console";
    }

    @Override
    public String getSyntax() {
        return "/origins dump";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (p.hasPermission("genesismc.origins.cmd.config.dump")) {
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC] DUMPING PLUGIN-API FILES:");
            getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "Loading config file:" +
                    GenesisDataFiles.getOrbCon().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.getPlugCon().getValues(Boolean.parseBoolean("all"))

            );
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC] Loading API");
            getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[GenesisMC] DUMPING SERVER FILES:" +
                    getServer().getVersion() +
                    getServer().getAllowEnd() +
                    getServer().getAllowNether() +
                    getServer().getPluginManager() +
                    getServer().getMaxPlayers() +
                    getServer().getConnectionThrottle() +
                    getServer().getLogger() +
                    getServer().getName() +
                    getServer().getBukkitVersion() +
                    getServer().getDefaultGameMode() +
                    getServer().getWorldType() +
                    getServer().getResourcePack() +
                    getServer().getHelpMap() +
                    getServer().getPluginManager().getPlugins()
            );
            p.sendMessage("Config info dumped into console.");
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
