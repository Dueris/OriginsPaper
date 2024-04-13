package me.dueris.calio.parse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.ObjectRemapper;
import me.dueris.calio.builder.inst.AccessorRoot;
import me.dueris.calio.builder.inst.factory.FactoryBuilder;
import me.dueris.calio.parse.verification.JsonFactoryValidator;
import me.dueris.calio.registry.impl.CalioRegistry;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.util.Arrays;

public class CalioJsonParser {

    /**
     * Parses the given directory, remaps JSON files, validates factory, creates instances and puts them into the registry.
     *
     * @param directory the directory to parse
     * @param root      the root accessor
     * @param namespace the namespace for the JSON files
     * @param before    the string to prepend to the JSON file names
     */
    public static void parseDirectory(File directory, AccessorRoot root, String namespace, String before) {
        Arrays.stream(directory.listFiles()).toList().forEach(jsonFile -> {
            try {
                if (!jsonFile.isDirectory()) {
                    NamespacedKey key = new NamespacedKey(namespace.toLowerCase(), (before + jsonFile.getName().replace(".json", "")).toLowerCase());
                    JsonObject remappedJSON = ObjectRemapper.createRemapped(jsonFile, key);
                    FactoryBuilder validatedFactory = JsonFactoryValidator.validateFactory(new FactoryBuilder(remappedJSON, jsonFile), root.getFactoryInst().getValidObjectFactory(), key);
                    if (validatedFactory != null) {
                        try {
                            root.getFactoryInst().createInstance(validatedFactory, jsonFile, CalioRegistry.INSTANCE.retrieve(root.getPutRegistry()), key);
                        } catch (Throwable throwable) {
                            String[] stacktrace = {"\n"};
                            Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).forEach(string -> stacktrace[0] += ("\tat " + string + "\n"));
                            CraftCalio.INSTANCE.getLogger().severe(
                                "An unhandled exception was thrown when attempting to create new Registerable!");
                            CraftCalio.INSTANCE.getLogger().severe(
                                "Registry: {a} | Associated Namespace: {b} | Throwable: {c}"
                                    .replace("{a}", root.getFactoryInst().getClass().getSimpleName())
                                    .replace("{b}", key.asString())
                                    .replace("{c}", throwable.getMessage()) + stacktrace[0]
                            );
                            System.out.println(new Gson().toJson(validatedFactory.getAsElement()));
                        }
                    }
                } else {
                    parseDirectory(jsonFile, root, namespace, jsonFile.getName() + "/");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
