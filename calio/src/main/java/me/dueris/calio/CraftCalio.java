package me.dueris.calio;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import me.dueris.calio.data.AccessorKey;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.FactoryHolder;
import me.dueris.calio.data.JsonObjectRemapper;
import me.dueris.calio.data.annotations.DontRegister;
import me.dueris.calio.data.annotations.RequiresPlugin;
import me.dueris.calio.parse.CalioJsonParser;
import me.dueris.calio.parse.reader.FileReader;
import me.dueris.calio.parse.reader.FileReaderFactory;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class CraftCalio {
	public static CraftCalio INSTANCE = new CraftCalio();
	public final ConcurrentHashMap<NamespacedKey, Pair<FactoryData, Class<? extends FactoryHolder>>> types = new ConcurrentHashMap<>();
	public final ArrayList<AccessorKey> keys = new ArrayList<>();
	private final List<File> datapackDirectoriesToParse = new ArrayList<>();
	private boolean isDebugging;

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
			this.keys.stream().sorted(Comparator.comparingInt(AccessorKey::getPriority)).forEach(accessorKey -> datapackDirectoriesToParse.forEach(root -> {
				for (File datapack : root.listFiles()) {
					try {
						FileReader fileReader = FileReaderFactory.createFileReader(datapack.toPath());
						if (fileReader == null) continue;
						List<String> files = fileReader.listFiles();
						HashMap<Pair<JsonObject, NamespacedKey>, Integer> newLoadingPrioritySortedMap = new HashMap<>();
						for (String file : files) {
							file = file.replace("/", "\\");
							if ((file.startsWith("data\\")) && file.endsWith(".json")) {
								String fixedJsonFile = file.substring(file.indexOf("data\\"));
								String namespace = fixedJsonFile.split("\\\\")[1];
								String name = fixedJsonFile.split("\\\\")[2].split("\\\\")[0];
								String key = fixedJsonFile.split("\\\\")[fixedJsonFile.split("\\\\").length - 1].replace(".json", "");
								if (accessorKey.getDirectory().equalsIgnoreCase(name)) {
									try (InputStream is = fileReader.getFileStream(file.replace("\\", "/"));
										 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
										StringBuilder line = new StringBuilder();
										String newLine;
										while ((newLine = br.readLine()) != null) {
											line.append(newLine);
										}
										String finishedLine = line.toString().replace("\n", "");
										JsonObject powerParser = JsonParser.parseReader(new StringReader(finishedLine)).getAsJsonObject();
										NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
										JsonObject remappedJsonObject = JsonObjectRemapper.remapJsonObject(powerParser, namespacedKey);
										newLoadingPrioritySortedMap.put(new Pair<>(remappedJsonObject, namespacedKey), remappedJsonObject.has("loading_priority") ? remappedJsonObject.getAsJsonPrimitive("loading_priority").getAsInt() : 0);
									}
								}
							}
						}

						List<Map.Entry<Pair<JsonObject, NamespacedKey>, Integer>> list = new ArrayList<>(newLoadingPrioritySortedMap.entrySet());
						Collections.sort(list, Map.Entry.comparingByValue());

						for (Map.Entry<Pair<JsonObject, NamespacedKey>, Integer> entry : list) {
							CalioJsonParser.initilize(entry.getKey(), accessorKey);
						}

						newLoadingPrioritySortedMap.clear();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}));
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
	 * Allows registering new FactoryHolders defined by a "type" field inside the root of the JSON OBJECT
	 */
	public void register(Class<? extends FactoryHolder> holder) {
		try {
			if (holder.isAnnotationPresent(DontRegister.class)) return;
			if (holder.isAnnotationPresent(RequiresPlugin.class)) {
				RequiresPlugin aN = holder.getAnnotation(RequiresPlugin.class);
				if (!org.bukkit.Bukkit.getPluginManager().isPluginEnabled(aN.pluginName())) return;
			}
			Method rC = holder.getDeclaredMethod("registerComponents", FactoryData.class);
			if (rC == null)
				throw new IllegalArgumentException("FactoryHolder doesn't have registerComponents method in it or its superclasses!");
			FactoryData data = (FactoryData) rC.invoke(null, new FactoryData());
			NamespacedKey identifier = data.getIdentifier();
			if (identifier == null)
				throw new IllegalArgumentException("Type identifier was not provided! FactoryHolder will not be loaded : " + holder.getSimpleName());
			this.types.put(identifier, new Pair<>(data, holder));
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ea) {
			if (ea instanceof NoSuchMethodException) return;
            ea.printStackTrace();
			throw new RuntimeException("An exception occured when registering FactoryHolder", ea);
		}
	}

	public void registerAccessor(String directory, int priority, boolean useTypeDefiner, Class<? extends FactoryHolder> typeOf, NamespacedKey registryKey) {
		keys.add(new AccessorKey(directory, priority, useTypeDefiner, registryKey, typeOf));
	}

	public void registerAccessor(String directory, int priority, boolean useTypeDefiner, NamespacedKey registryKey) {
		keys.add(new AccessorKey(directory, priority, useTypeDefiner, registryKey, null));
	}
}
