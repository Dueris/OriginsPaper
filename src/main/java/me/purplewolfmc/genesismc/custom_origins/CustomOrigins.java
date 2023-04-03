package me.purplewolfmc.genesismc.custom_origins;

import me.purplewolfmc.genesismc.core.files.GenesisDataFiles;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.bukkit.Bukkit.getServer;

public class CustomOrigins implements Listener {

    public static void onEnableCusotmOrigins() {
        File custom_folder = new File(getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");
        if(custom_folder.listFiles().length >= 1){
            DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(Path file) throws IOException {
                    return (Files.isDirectory(file));
                }
            };
            Path dir = FileSystems.getDefault().getPath(String.valueOf(getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + "/custom_origins/"));
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
                for (Path path : stream) {
                    //begin checks
                    if(path.toFile().exists()){
                        if(path.toFile().list().length >= 2){
                            if (GenesisDataFiles.getPlugCon().getString("console-dump-onstartup").equalsIgnoreCase("true")) {
                                getServer().getConsoleSender().sendMessage("there are origins (" + path.toFile().list().length + ")");
                            }else{
                                //no message response
                            }
                            String maindir = path.toFile().getAbsolutePath();
                            if (GenesisDataFiles.getPlugCon().getString("console-dump-onstartup").equalsIgnoreCase("true")) {
                                getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "[GenesisMC] Loading custom origins in dir of " + path.toFile().getAbsolutePath());
                            }
                            File originpack_datamain = new File(String.valueOf(path), "data");
                            String datapath = originpack_datamain.getAbsolutePath();
                            File originpack_powermain = new File(String.valueOf(path), "powers");
                            String powerspath = originpack_powermain.getAbsolutePath();
                            File originpack_mainclass = new File(String.valueOf(datapath), "main.yml");
                            if(originpack_datamain.exists()){
                                //check 1
                                if (GenesisDataFiles.getPlugCon().getString("console-dump-onstartup").equalsIgnoreCase("true")) {
                                    getServer().getConsoleSender().sendMessage("data folder loaded. check 1 complete");
                                }else{
                                    //do nothing
                                }
                                if(originpack_powermain.exists()){
                                    //check 2
                                    if (GenesisDataFiles.getPlugCon().getString("console-dump-onstartup").equalsIgnoreCase("true")) {
                                        getServer().getConsoleSender().sendMessage("powers folder loaded. check 2 complete");
                                    }
                                    if(originpack_mainclass.exists()){
                                        //check 3
                                        if (GenesisDataFiles.getPlugCon().getString("console-dump-onstartup").equalsIgnoreCase("true")) {
                                            getServer().getConsoleSender().sendMessage("main class loaded. check 3 complete");
                                        }
                                        YamlConfiguration mainconfig = new YamlConfiguration();
                                        mainconfig = YamlConfiguration.loadConfiguration(originpack_mainclass);
                                        if(mainconfig.getString("is-custom-origin").equalsIgnoreCase("true")){
                                            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC] Successfully loaded " + originpack_mainclass.getParentFile().getParentFile().getName());

                                        }else{
                                            getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Origin pack is invalid");
                                            getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Disabling origin pack");
                                            //do nothing
                                        }

                                        }else{
                                        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Error loading custom origin: CODE-001");
                                    }
                                }else{
                                    getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Error loading custom origin: CODE-002");
                                }
                            }else{
                                getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Error loading custom origin: CODE-003");
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            getServer().getConsoleSender().sendMessage("[GenesisMC] No custom origins loaded");
        }

    }

}
