package me.dueris.calio;

import me.dueris.calio.builder.CalioBuilder;
import me.dueris.calio.parse.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

import static me.dueris.calio.ParsingStage.*;

public class CraftCalio {
    private ParsingStage state;
    private boolean isDebugging;
    public static CraftCalio INSTANCE = new CraftCalio();
    public static Path DATAPACk_DIR = MinecraftServer.getServer().getWorldPath(LevelResource.DATAPACK_DIR).toFile().toPath();

    public static NamespacedKey bukkitIdentifier(String namespace, String path){
        return NamespacedKey.fromString(namespace + ":" + path);
    }

    public static ResourceLocation nmsIdentifier(String namespace, String path){
        return new ResourceLocation(namespace, path);
    }

    public void start(boolean debug){
        this.isDebugging = debug;
        setState(PRE);
        debug("Starting CraftCalio parser...");
        for(File datapack : DATAPACk_DIR.toFile().listFiles()){
            if(!datapack.isDirectory()) continue;
            for(File data : datapack.listFiles()){
                if(!data.getName().equalsIgnoreCase("data") || !data.isDirectory()) continue;
                // Parse namespace
                String namespace;
                for(File namespacedFile : data.listFiles()){
                    if(!data.isDirectory()) continue;
                    namespace = namespacedFile.getName();
                    // Inside namespace folder
                    for(File ff : namespacedFile.listFiles()){
                        getBuilder().folderToFactory.forEach((folder, factory) -> {
                            if(ff.getName().equalsIgnoreCase(folder)){
                                JsonParser.parseDirectory(ff, factory);
                            }
                        });
                    }
                }
            }
        }
        // Load factories
    }

    public void debug(String msg){
        if(isDebugging){
            getLogger().info(msg);
        }
    }

    public Logger getLogger(){
        return Logger.getLogger("CraftCalio");
    }

    public CalioBuilder getBuilder(){
        return CalioBuilder.INSTANCE;
    }

    public boolean isPrelaunch(){
        return this.state.equals(PRE);
    }

    public boolean isParsing(){
        return this.state.equals(PARSE);
    }

    public boolean isBuilding(){
        return this.state.equals(BUILD);
    }

    public boolean isRegistering(){
        return this.state.equals(REGISTER);
    }

    public boolean isFinished(){
        return this.state.equals(POST);
    }

    public ParsingStage getState(){
        return this.state;
    }

    private void setState(ParsingStage state){
        this.state = state;
    }
}
