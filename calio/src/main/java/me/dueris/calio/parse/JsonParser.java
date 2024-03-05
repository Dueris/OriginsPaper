package me.dueris.calio.parse;

import me.dueris.calio.builder.NamespaceRemapper;
import me.dueris.calio.builder.inst.AccessorRoot;
import me.dueris.calio.builder.inst.FactoryProvider;
import me.dueris.calio.parse.verification.JsonFactoryValidator;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.registry.impl.CalioRegistry;

import org.bukkit.NamespacedKey;
import org.json.simple.JSONObject;

import com.google.gson.JsonElement;

import java.io.File;
import java.util.Arrays;

public class JsonParser {
    public static void parseDirectory(File directory, AccessorRoot root, String namespace){
        Arrays.stream(directory.listFiles()).filter(file -> !file.isDirectory()).toList().forEach(jsonFile -> {
            try {
                JSONObject remappedJSON = NamespaceRemapper.createRemapped(jsonFile);
                NamespacedKey key = new NamespacedKey(namespace, jsonFile.getName().replace(".json", ""));
                FactoryProvider validatedFactory = JsonFactoryValidator.validateFactory(new FactoryProvider(remappedJSON), root.getFactoryInst().getValidObjectFactory(), key);
                if(validatedFactory != null){
                    root.getFactoryInst().createInstance(validatedFactory, jsonFile, CalioRegistry.INSTANCE.retrieve(root.getPutRegistry()), key);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
