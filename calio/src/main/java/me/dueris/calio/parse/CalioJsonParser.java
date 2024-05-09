package me.dueris.calio.parse;

import com.google.gson.JsonObject;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.JsonObjectRemapper;
import me.dueris.calio.builder.inst.*;
import me.dueris.calio.builder.inst.factory.FactoryBuilder;
import me.dueris.calio.parse.verification.JsonFactoryValidator;
import me.dueris.calio.registry.impl.CalioRegistry;
import org.bukkit.NamespacedKey;
import oshi.util.tuples.Pair;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class CalioJsonParser {

    protected static HashMap<Pair<FactoryBuilder, NamespacedKey>, Integer> loadingPrioritySortedMap = new HashMap<>();
    protected static HashMap<Pair<JsonObject, NamespacedKey>, Integer> newLoadingPrioritySortedMap = new HashMap<>();

    /**
     * Parses the given directory, remaps JSON files, validates factory, creates instances and puts them into the registry.
     *
     * @param directory the directory to parse
     * @param root      the root accessor
     * @param namespace the namespace for the JSON files
     * @param before    the string to prepend to the JSON file names
     */
    @Deprecated(forRemoval = true)
    public static void parseDirectory(File directory, AccessorRoot root, String namespace, String before, boolean createInstances) {
        Arrays.stream(directory.listFiles()).toList().forEach(jsonFile -> {
            try {
                if (!jsonFile.isDirectory()) {
                    NamespacedKey key = new NamespacedKey(namespace.toLowerCase(), (before + jsonFile.getName().replace(".json", "")).toLowerCase());
                    JsonObject remappedJSON = JsonObjectRemapper.createRemapped(jsonFile, key);
                    FactoryBuilder builder = JsonFactoryValidator.validateFactory(new FactoryBuilder(remappedJSON, jsonFile), root.getFactoryInst().getValidObjectFactory(), key);
                    if (builder != null) {
                        loadingPrioritySortedMap.put(new Pair<>(builder, key), builder.getRoot().getNumberOrDefault("loading_priority", 0).getInt());
                    }
                } else {
                    parseDirectory(jsonFile, root, namespace, jsonFile.getName() + "/", false);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        if (createInstances) {
            List<Map.Entry<Pair<FactoryBuilder, NamespacedKey>, Integer>> list = new ArrayList<>(loadingPrioritySortedMap.entrySet());
            Collections.sort(list, Map.Entry.comparingByValue());

            for (Map.Entry<Pair<FactoryBuilder, NamespacedKey>, Integer> entry : list) {
                FactoryBuilder validatedFactory = entry.getKey().getA();
                File jsonFile = validatedFactory.getJsonFile();
                NamespacedKey key = entry.getKey().getB();

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
                }
            }

            loadingPrioritySortedMap.clear();
        }
    }
    // New calio

    /**
     * Parses the given directory, remaps JSON files, validates factory, creates instances and puts them into the registry.
     *
     * @param directory the directory to parse
     * @param root      the root accessor
     * @param namespace the namespace for the JSON files
     * @param before    the string to prepend to the JSON file names
     */
    public static void parsePackDirectory(File directory, AccessorKey root, String namespace, String before, boolean createInstances) {
        Arrays.stream(directory.listFiles()).toList().forEach(jsonFile -> {
            try {
                if (!jsonFile.isDirectory()) {
                    NamespacedKey key = new NamespacedKey(namespace.toLowerCase(), (before + jsonFile.getName().replace(".json", "")).toLowerCase());
                    JsonObject remappedJSON = JsonObjectRemapper.createRemapped(jsonFile, key);
                    newLoadingPrioritySortedMap.put(new Pair<>(remappedJSON, key), remappedJSON.has("loading_priority") ? remappedJSON.getAsJsonPrimitive("loading_priority").getAsInt() : 0);
                } else {
                    parsePackDirectory(jsonFile, root, namespace, jsonFile.getName() + "/", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (createInstances) {
            List<Map.Entry<Pair<JsonObject, NamespacedKey>, Integer>> list = new ArrayList<>(newLoadingPrioritySortedMap.entrySet());
            Collections.sort(list, Map.Entry.comparingByValue());

            for (Map.Entry<Pair<JsonObject, NamespacedKey>, Integer> entry : list) {
                NamespacedKey key = entry.getKey().getB();
                if (root.getOfType() == null) continue;

                try {
                    FactoryData data;
                    if (root.isUsesTypeDefiner()) {
                        if (!CraftCalio.INSTANCE.types.containsKey(NamespacedKey.fromString(entry.getKey().getA().get("type").getAsString()))) {
                            CraftCalio.INSTANCE.getLogger().severe("Unknown type was provided! : {a} | {b}"
                                .replace("{a}", NamespacedKey.fromString(entry.getKey().getA().get("type").getAsString()).asString())
                                .replace("{b}", key.asString())
                            );
                            continue;
                        } else {
                            data = CraftCalio.INSTANCE.types.get(NamespacedKey.fromString(entry.getKey().getA().get("type").getAsString()));
                        }
                    } else {
                        // We gotta invoke the FactoryData manually
                        Class<? extends FactoryHolder> holder = root.getOfType();
                        Method rC = holder.getDeclaredMethod("registerComponents", FactoryData.class);
                        if (rC == null) throw new IllegalArgumentException("FactoryHolder doesn't have registerComponents method in it or its superclasses!");
                        if (holder.isAnnotationPresent(RequiresPlugin.class)) {
                            RequiresPlugin aN = holder.getAnnotation(RequiresPlugin.class);
                            if (!org.bukkit.Bukkit.getPluginManager().isPluginEnabled(aN.pluginName())) return;
                        }
                        data = (FactoryData) rC.invoke(null, new FactoryData());
                    }

                    // Create the constructor
                    Class<? extends FactoryHolder> holder = root.getOfType();
                    Constructor<?> constructor = findConstructor(data, holder);
                    System.out.println("Constructor found? : " + constructor != null);
                } catch (Throwable throwable) {
                    String[] stacktrace = {"\n"};
                    Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).forEach(string -> stacktrace[0] += ("\tat " + string + "\n"));
                    CraftCalio.INSTANCE.getLogger().severe(
                        "An unhandled exception was thrown when attempting to create new Registerable!");
                    CraftCalio.INSTANCE.getLogger().severe(
                        "Registry: {a} | Associated Namespace: {b} | Throwable: {c}"
                            .replace("{a}", root.getOfType().getSimpleName())
                            .replace("{b}", key.asString())
                            .replace("{c}", throwable.getMessage()) + stacktrace[0]
                    );
                }
            }

            newLoadingPrioritySortedMap.clear();
        }
    }

    private static Constructor<?> findConstructor(FactoryData data, Class<? extends FactoryHolder> holder) {
        Class<?>[] params = Arrays.stream(data.getProviders()).map(FactoryDataDefiner::getType).toList().toArray(new Class<?>[0]);
		try {
			return holder.getConstructor(params);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
        return null;
	}

}
