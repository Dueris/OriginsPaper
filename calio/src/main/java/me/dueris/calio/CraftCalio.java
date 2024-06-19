package me.dueris.calio;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import me.dueris.calio.data.*;
import me.dueris.calio.data.AssetIdentifier.AssetType;
import me.dueris.calio.data.annotations.DontRegister;
import me.dueris.calio.data.annotations.RequiresPlugin;
import me.dueris.calio.parse.CalioJsonParser;
import me.dueris.calio.parse.reader.FileReader;
import me.dueris.calio.parse.reader.FileReaderFactory;
import me.dueris.calio.registry.Registrable;
import me.dueris.calio.registry.RegistryKey;
import me.dueris.calio.registry.impl.CalioRegistry;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class CraftCalio {
	public static CraftCalio INSTANCE = new CraftCalio();
	public final ConcurrentHashMap<NamespacedKey, Pair<FactoryData, Class<? extends FactoryHolder>>> types = new ConcurrentHashMap<>();
	public final ArrayList<AccessorKey> keys = new ArrayList<>();
	public final ArrayList<AssetIdentifier> assetKeys = new ArrayList<>();
	private final List<File> datapackDirectoriesToParse = new ArrayList<>();
	private boolean isDebugging;

	public static NamespacedKey bukkitIdentifier(String namespace, String path) {
		return NamespacedKey.fromString(namespace + ":" + path);
	}

	public static ResourceLocation nmsIdentifier(String namespace, String path) {
		return ResourceLocation.fromNamespaceAndPath(namespace, path);
	}

	/**
	 * Add a datapack path to the list of directories to parse.
	 *
	 * @param path the path to be added
	 */
	public void addDatapackPath(Path path) {
		datapackDirectoriesToParse.add(path.toFile());
	}

	/**
	 * A method to start parsing with a provided debug mode and ExecutorService.
	 *
	 * @param debug a boolean indicating whether debugging is enabled
	 */
	public void start(boolean debug) {
		this.isDebugging = debug;
		debug("Starting CraftCalio parser...");
		this.assetKeys.stream().sorted(Comparator.comparingInt(AssetIdentifier::priority)).forEach(assetKey -> {
			datapackDirectoriesToParse.forEach(root -> {
				packLoop:
				for (File datapack : root.listFiles()) {
					try {
						FileReader reader = FileReaderFactory.createFileReader(datapack.toPath());
						if (reader == null) continue;
						List<String> files = reader.listFiles();
						for (String file : files) {
							file = file.replace("/", "\\");
							if ((file.startsWith("assets\\")) && file.endsWith(assetKey.fileType())) {
								String fixedJsonFile = file.substring(file.indexOf("assets\\"));
								String[] parts = fixedJsonFile.split("\\\\");
								String namespace = parts[1];
								String name = fixedJsonFile.split("\\\\")[2].split("\\\\")[0];
								String key = assetKey.directory() + "/" + fixedJsonFile.substring(fixedJsonFile.indexOf(namespace) + namespace.length() + 1).replace("\\", "/").replace(name + "/", "");
								if (assetKey.directory().equalsIgnoreCase(name)) {
									try (InputStream is = reader.getFileStream(file.replace("\\", "/"));
										 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
										// Input stream made.
										Class<? extends Registrable> toInvoke = assetKey.registryKey().type();
										NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
										switch (assetKey.assetType()) {
											case JSON -> {
												StringBuilder line = new StringBuilder();
												String newLine;
												while ((newLine = br.readLine()) != null) {
													line.append(newLine);
												}
												String finishedLine = line.toString().replace("\n", "");
												JsonObject assetParser;
												try {
													assetParser = JsonParser.parseReader(new StringReader(finishedLine)).getAsJsonObject();
												} catch (Throwable throwable) {
													getLogger().severe("An unhandled exception occurred when parsing a json file! Invalid syntax? The datapack will not be loaded.");
													continue packLoop;
												}
												CalioRegistry.INSTANCE.retrieve(assetKey.registryKey()).register(toInvoke.getConstructor(NamespacedKey.class, JsonObject.class).newInstance(namespacedKey, assetParser));
											}
											case IMAGE -> {
												CalioRegistry.INSTANCE.retrieve(assetKey.registryKey()).register(toInvoke.getConstructor(NamespacedKey.class, BufferedImage.class).newInstance(namespacedKey, ImageIO.read(is)));
											}
										}
									} catch (Throwable throwable) {
										throwable.printStackTrace();
									}
								}
							}
						}
					} catch (Throwable throwable) {
						throwable.printStackTrace();
					}
				}
			});
		});
		this.keys.stream().sorted(Comparator.comparingInt(AccessorKey::getPriority)).forEach(accessorKey -> datapackDirectoriesToParse.forEach(root -> {
			for (File datapack : root.listFiles()) {
				try {
					FileReader fileReader = FileReaderFactory.createFileReader(datapack.toPath());
					if (fileReader == null) continue;
					List<String> files = fileReader.listFiles();
					HashMap<Pair<JsonObject, NamespacedKey>, Integer> newLoadingPrioritySortedMap = new HashMap<>();
					fileLoop:
					for (String file : files) {
						file = file.replace("/", "\\");
						if ((file.startsWith("data\\")) && file.endsWith(".json")) {
							String fixedJsonFile = file.substring(file.indexOf("data\\"));
							String[] parts = fixedJsonFile.split("\\\\");
							String namespace = parts[1];
							String name = fixedJsonFile.split("\\\\")[2].split("\\\\")[0];
							String key = fixedJsonFile.substring(fixedJsonFile.indexOf(namespace) + namespace.length() + 1).replace(".json", "").replace("\\", "/").replace(name + "/", "");
							if (accessorKey.getDirectory().equalsIgnoreCase(name)) {
								try (InputStream is = fileReader.getFileStream(file.replace("\\", "/"));
									 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
									StringBuilder line = new StringBuilder();
									String newLine;
									while ((newLine = br.readLine()) != null) {
										line.append(newLine);
									}
									String finishedLine = line.toString().replace("\n", "");
									if (isCorrupted(finishedLine)) {
										getLogger().severe("The json file with ResourceLocation of \"{}\" is corrupted! Please contact the pack author.".replace("{}", namespace + ":" + key));
										continue fileLoop;
									}
									JsonObject powerParser;
									try {
										powerParser = JsonParser.parseReader(new StringReader(finishedLine)).getAsJsonObject();
									} catch (Throwable throwable) {
										getLogger().severe("An unhandled exception occurred when parsing a json file \"{}\"! Invalid syntax? The datapack will not be loaded.".replace("{}", namespace + ":" + key));
										continue fileLoop;
									}
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
	}

	private boolean isCorrupted(String finishedLine) {
		try {
			JsonElement jsonElement = JsonParser.parseString(finishedLine);
			if (!jsonElement.isJsonObject()) {
				return true;
			}
		} catch (JsonSyntaxException e) {
			return true;
		}
		return false;
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
			ClassLoader classLoader = CraftCalio.class.getClassLoader();
			classLoader.loadClass(holder.getName()); // Preload class
			Method rC = holder.getDeclaredMethod("registerComponents", FactoryData.class);
			if (rC == null)
				throw new IllegalArgumentException("FactoryHolder doesn't have registerComponents method in it or its superclasses!");
			FactoryData data = (FactoryData) rC.invoke(null, new FactoryData());
			NamespacedKey identifier = data.getIdentifier();
			if (identifier == null)
				throw new IllegalArgumentException("Type identifier was not provided! FactoryHolder will not be loaded : " + holder.getSimpleName());
			this.types.put(identifier, new Pair<>(data, holder));
		} catch (ClassNotFoundException ea) {
			throw new RuntimeException("Unable to resolve class during registration!", ea);
		} catch (Throwable ea) {
			if (ea instanceof NoSuchMethodException) return;
			if (ea instanceof IllegalArgumentException) {
				getLogger().severe("Type provider was not provided! FactoryHolder will not be loaded. : " + holder.getSimpleName());
			}
			ea.printStackTrace();
			throw new RuntimeException("An exception occured when registering FactoryHolder", ea);
		}
	}

	public <T extends Registrable> void registerAccessor(String directory, int priority, boolean useTypeDefiner, Class<? extends FactoryHolder> typeOf, RegistryKey<T> registryKey, String defaultType) {
		keys.add(new AccessorKey<T>(directory, priority, useTypeDefiner, registryKey, typeOf, defaultType));
	}

	public <T extends Registrable> void registerAccessor(String directory, int priority, boolean useTypeDefiner, Class<? extends FactoryHolder> typeOf, RegistryKey<T> registryKey) {
		keys.add(new AccessorKey<T>(directory, priority, useTypeDefiner, registryKey, typeOf, null));
	}

	public <T extends Registrable> void registerAsset(String directory, int priority, String fileType, AssetType assetType, RegistryKey<T> registryKey) {
		assetKeys.add(new AssetIdentifier<>(directory, priority, fileType, assetType, registryKey));
	}
}
