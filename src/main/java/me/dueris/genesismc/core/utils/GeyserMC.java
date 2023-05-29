package me.dueris.genesismc.core.utils;

import org.bukkit.Bukkit;

import java.io.IOException;

import static org.bukkit.Bukkit.getServer;

public class GeyserMC {

    public static void downloadGeyserPack(){
        if(getServer().getPluginManager().isPluginEnabled("Geyser-Spigot")){
            String saveDirectory = Bukkit.getServer().getPluginManager().getPlugin("Geyser-Spigot").getDataFolder() + "/packs/";
            try {
                BukkitUtils.downloadFileFromURL("https://ci.opencollab.dev/job/GeyserMC/job/GeyserOptionalPack/job/master/lastSuccessfulBuild/artifact/GeyserOptionalPack.mcpack", saveDirectory);
                System.out.println("GeyserPack downloaded successfully");
            } catch (IOException e) {
                System.out.println("Error downloading file: " + e.getMessage());
            }
        }

    }

}
