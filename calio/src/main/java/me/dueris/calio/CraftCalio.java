package me.dueris.calio;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.bukkit.NamespacedKey;

public class CraftCalio {

    public static NamespacedKey bukkitIdentifier(String namespace, String path){
        return NamespacedKey.fromString(namespace + ":" + path);
    }

    public static ResourceLocation nmsIdentifier(String namespace, String path){
        return new ResourceLocation(namespace, path);
    }
}
