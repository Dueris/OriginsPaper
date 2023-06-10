package me.dueris.genesismc.core;

import me.dueris.genesismc.core.factory.OriginStartHandler;
import org.bukkit.Bukkit;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class VersionControl {

    public static boolean isCompatiblePaper(){

        try {
            Class.forName("com.destroystokyo.paper.event.player.PlayerElytraBoostEvent");
                return true;
        } catch (ClassNotFoundException e) {
            getLogger().severe("This server is unable to start GenesisMC, disabling..");
            getServer().getPluginManager().disablePlugin(GenesisMC.getPlugin());
            return false;
        }

    }

    public static final String REQUIRED_VERSION = "1.20";

    public static boolean isCorrectVersion(){
        String serverVersion = Bukkit.getVersion();
    return serverVersion.contains(REQUIRED_VERSION);
    }

}
