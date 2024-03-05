package me.dueris.calio;

import me.dueris.calio.builder.CalioBuilder;
import me.dueris.calio.builder.inst.AccessorRoot;
import me.dueris.calio.parse.JsonParser;
import net.minecraft.resources.ResourceLocation;

import org.bukkit.NamespacedKey;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import static me.dueris.calio.ParsingStage.*;

public class CraftCalio {
    private ParsingStage state;
    private boolean isDebugging;
    public static CraftCalio INSTANCE = new CraftCalio();
    private List<File> datapackDirectoriesToParse = new ArrayList();

    public static NamespacedKey bukkitIdentifier(String namespace, String path){
        return NamespacedKey.fromString(namespace + ":" + path);
    }

    public static ResourceLocation nmsIdentifier(String namespace, String path){
        return new ResourceLocation(namespace, path);
    }

    public void addDatapackPath(Path path){
        datapackDirectoriesToParse.add(path.toFile());
    }

    public void start(boolean debug, ExecutorService threadPool){
        this.isDebugging = debug;
        Runnable parser = () -> {
            setState(ParsingStage.PRE);
            debug("Starting CraftCalio parser...");
            setState(ParsingStage.PARSE);
            // Collections
            getBuilder().accessorRoots.stream().sorted(Comparator.comparingInt(AccessorRoot::getPriority)).toList().forEach((root) -> {
                datapackDirectoriesToParse.forEach(rootFolder -> {
                    for(File datapack : rootFolder.listFiles()){
                        if(!datapack.isDirectory()) continue;
                        for(File data : datapack.listFiles()){
                            if(!data.getName().equalsIgnoreCase("data") || !data.isDirectory()) continue;
                            // Parse namespaced factories
                            String namespace;
                            for(File namespacedFile : data.listFiles()){
                                if(!namespacedFile.isDirectory()) continue;
                                namespace = namespacedFile.getName();
                                // Inside namespace folder
                                for(File ff : namespacedFile.listFiles()){
                                    if(!ff.isDirectory()) continue;
                                        if(root.getDirectoryPath().equalsIgnoreCase(ff.getName())){
                                            JsonParser.parseDirectory(ff, root, namespace);
                                        }
                                }
                            }
                        }
                    }
                });
            });
        };
        if(threadPool != null){
            CompletableFuture future = CompletableFuture.runAsync(parser, threadPool);
            try {
                future.join();
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                this.getLogger().severe("An Error occured during parsing, printing stacktrace:");
                e.printStackTrace();
            }
        }else{
            parser.run();
        }
    }

    public void start(boolean debug){
        this.start(debug, null);
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
