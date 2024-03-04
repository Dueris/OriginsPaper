package me.dueris.calio;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.bukkit.NamespacedKey;

import java.util.logging.Logger;

public class CraftCalio {

    public static NamespacedKey bukkitIdentifier(String namespace, String path){
        return NamespacedKey.fromString(namespace + ":" + path);
    }

    public static ResourceLocation nmsIdentifier(String namespace, String path){
        return new ResourceLocation(namespace, path);
    }

    public static void start(){
        getLogger().info("Starting CraftCalio parser...");
        // Load factories
    }

    public static Logger getLogger(){
        return Logger.getLogger("CraftCalio");
    }
}
