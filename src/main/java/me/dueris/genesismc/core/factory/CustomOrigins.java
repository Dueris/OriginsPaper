package me.dueris.genesismc.core.factory;

import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.bukkit.Bukkit.getServer;

public class CustomOrigins implements Listener {

    public static void onEnableCustomOrigins() {
//        File custom_folder = new File(getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");
//        if(custom_folder.listFiles().length >= 1){
//            DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
//                @Override
//                public boolean accept(Path file) throws IOException {
//                    return (Files.isDirectory(file));
//                }
//            };
//            Path dir = FileSystems.getDefault().getPath(String.valueOf(getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + "/custom_origins/"));
//            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
//                for (Path path : stream) {
//                    //begin checks
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }

}
