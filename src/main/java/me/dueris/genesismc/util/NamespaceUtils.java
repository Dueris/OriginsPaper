package me.dueris.genesismc.util;

import org.bukkit.NamespacedKey;

public class NamespaceUtils {
    public static NamespacedKey getDynamicNamespace(String currentNamespace, String dynamicNamespace){
        if(dynamicNamespace.contains("*")){
            String[] split = dynamicNamespace.split(":");
            String namespace = split[0];
            String path = split[1];
            if(path.contains("*")){
                path.replace("*", NamespacedKey.fromString(currentNamespace).asString().split(":")[1]);
            }
            if(namespace.contains("*")){
                namespace.replace("*", NamespacedKey.fromString(currentNamespace).asString().split(":")[0]);
            }
        }
        return NamespacedKey.fromString(dynamicNamespace);
    }

    public static NamespacedKey getNamespaceStringAsBukkit(String key){
        return NamespacedKey.fromString(key);
    }
}
