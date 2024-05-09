package me.dueris.calio;

import me.dueris.calio.builder.CalioBuilder;
import me.dueris.calio.builder.inst.*;
import me.dueris.calio.parse.CalioJsonParser;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class CraftCalio {
    public static CraftCalio INSTANCE = new CraftCalio();
    private final List<File> datapackDirectoriesToParse = new ArrayList<>();
    private boolean isDebugging;
    public final ConcurrentHashMap<NamespacedKey, FactoryData> types = new ConcurrentHashMap<>();
    public final ArrayList<AccessorKey> keys = new ArrayList<>();

    public static NamespacedKey bukkitIdentifier(String namespace, String path) {
        return NamespacedKey.fromString(namespace + ":" + path);
    }

    public static ResourceLocation nmsIdentifier(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    /**
     * Add a datapack path to the list of directories to parse.
     *
     * @param path the path to be added
     * @return void
     */
    public void addDatapackPath(Path path) {
        datapackDirectoriesToParse.add(path.toFile());
    }

    /**
     * A method to start parsing with a provided debug mode and ExecutorService.
     *
     * @param debug      a boolean indicating whether debugging is enabled
     * @param threadPool an ExecutorService for managing threads
     */
    public void start(boolean debug, ExecutorService threadPool) {
        this.isDebugging = debug;
        Runnable parser = () -> {
            debug("Starting CraftCalio parser...");
            // New Calio
            this.keys.stream().sorted(Comparator.comparingInt(AccessorKey::getPriority)).forEach(accessorKey -> {
                datapackDirectoriesToParse.forEach(root -> {
                    for (File datapack : root.listFiles()) {
                        if (!datapack.isDirectory()) continue;
                        for (File data : datapack.listFiles()) {
                            if (!data.getName().equalsIgnoreCase("data") || !data.isDirectory()) continue;
                            // Parse namespaced factories
                            String namespace;
                            for (File namespacedFile : data.listFiles()) {
                                if (!namespacedFile.isDirectory()) continue;
                                namespace = namespacedFile.getName();
                                for (File k : namespacedFile.listFiles()) {
                                    if (k.getName().equalsIgnoreCase(accessorKey.getDirectory())) {
                                        CalioJsonParser.parsePackDirectory(k, accessorKey, namespace, "", true);
                                    }
                                }
                            }
                        }
                    }
                });
            });
            // Collections
            /*
            getBuilder().accessorRoots.stream().sorted(Comparator.comparingInt(AccessorRoot::getPriority)).toList().forEach((root) -> {
                datapackDirectoriesToParse.forEach(rootFolder -> {
                    for (File datapack : rootFolder.listFiles()) {
                        if (!datapack.isDirectory()) continue;
                        for (File data : datapack.listFiles()) {
                            if (!data.getName().equalsIgnoreCase("data") || !data.isDirectory()) continue;
                            // Parse namespaced factories
                            String namespace;
                            for (File namespacedFile : data.listFiles()) {
                                if (!namespacedFile.isDirectory()) continue;
                                namespace = namespacedFile.getName();
                                // Inside namespace folder
                                for (File ff : namespacedFile.listFiles()) {
                                    if (!ff.isDirectory()) continue;
                                    if (root.getDirectoryPath().equalsIgnoreCase(ff.getName())) {
                                        CalioJsonParser.parseDirectory(ff, root, namespace, "", true);
                                    }
                                }
                            }
                        }
                    }
                });
            });
             */
        };
        if (threadPool != null) {
            CompletableFuture future = CompletableFuture.runAsync(parser, threadPool);
            try {
                future.join();
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                this.getLogger().severe("An Error occured during parsing, printing stacktrace:");
                e.printStackTrace();
            }
        } else {
            parser.run();
        }
    }

    /**
     * Starts the CraftCalio parser with optional debugging.
     *
     * @param debug a boolean indicating whether debug mode is enabled or disabled
     */
    public void start(boolean debug) {
        this.start(debug, null);
    }

    /**
     * Logs a debug message if debugging is enabled.
     *
     * @param msg the debug message to be logged
     */
    public void debug(String msg) {
        if (isDebugging) {
            getLogger().info(msg);
        }
    }

    /**
     * Returns the Logger object for the "CraftCalio" logger.
     *
     * @return the Logger object for "CraftCalio"
     */
    public Logger getLogger() {
        return Logger.getLogger("CraftCalio");
    }

    /**
     * Retrieves the instance of the CalioBuilder.
     *
     * @return the CalioBuilder instance
     */
    public CalioBuilder getBuilder() {
        return CalioBuilder.INSTANCE;
    }

    /**
     * Allows registering new FactoryHolders defined by a "type" field inside the root of the JSON OBJECT
     */
    public void register(Class<? extends FactoryHolder> holder) {
        try {
            Method rC = holder.getDeclaredMethod("registerComponents", FactoryData.class);
            if (rC == null) throw new IllegalArgumentException("FactoryHolder doesn't have registerComponents method in it or its superclasses!");
            if (holder.isAnnotationPresent(RequiresPlugin.class)) {
                RequiresPlugin aN = holder.getAnnotation(RequiresPlugin.class);
                if (!org.bukkit.Bukkit.getPluginManager().isPluginEnabled(aN.pluginName())) return;
            }
            FactoryData data = (FactoryData) rC.invoke(null, new FactoryData());
            NamespacedKey identifier = data.getIdentifier();
            if (identifier == null) throw new IllegalArgumentException("Type identifier was not provided! FactoryHolder will not be loaded : " + holder.getSimpleName());
            this.types.put(identifier, data);
            System.out.println("new FactoryHolder registered! " + holder.getSimpleName());
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ea) {
            if (ea instanceof NoSuchMethodException) return;
			throw new RuntimeException("An exception occured when registering FactoryHolder", ea);
		}
	}

    private boolean hasRegisterMethod(Class<?> clz) {
        try {
            Method method = clz.getDeclaredMethod("registerComponents", FactoryData.class);
            int modifiers = method.getModifiers();
            return Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers);
        } catch (Exception e) {
            return false;
        }
    }

    public void registerAccessor(String directory, int priority, boolean useTypeDefiner, Class<? extends FactoryHolder> typeOf) {
        keys.add(new AccessorKey(directory, priority, useTypeDefiner, typeOf));
    }

    public void registerAccessor(String directory, int priority, boolean useTypeDefiner) {
        keys.add(new AccessorKey(directory, priority, useTypeDefiner, null));
    }
}
