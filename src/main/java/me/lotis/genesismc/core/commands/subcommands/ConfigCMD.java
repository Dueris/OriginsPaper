package me.lotis.genesismc.core.commands.subcommands;

import me.lotis.genesismc.core.files.GenesisDataFiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Bukkit.getServer;

public class ConfigCMD extends SubCommand {
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
        return "/origins config";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (p.hasPermission("genesismc.origins.cmd.config.dump")) {
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC] DUMPING PLUGIN-API FILES:");
            getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "Loading config file:" +
                    getPluginManager().getPlugin("genesismc").getConfig().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get1().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get2().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get3().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get4().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get5().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get6().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get7().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get8().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get9().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get10().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get11().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get12().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get13().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get14().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get15().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get16().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.get17().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.getOrb().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.getBeta().getValues(Boolean.parseBoolean("all")) +
                    GenesisDataFiles.getMenu().getValues(Boolean.parseBoolean("all")) +
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
}
