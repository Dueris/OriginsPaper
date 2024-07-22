package me.dueris.calio;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import me.dueris.calio.data.*;
import me.dueris.calio.data.annotations.DontRegister;
import me.dueris.calio.data.annotations.RequiresPlugin;
import me.dueris.calio.parse.CalioJsonParser;
import me.dueris.calio.parse.reader.FileReader;
import me.dueris.calio.parse.reader.FileReaderFactory;
import me.dueris.calio.registry.Registrable;
import me.dueris.calio.registry.RegistryKey;
import me.dueris.calio.registry.impl.CalioRegistry;
import me.dueris.calio.util.holders.Pair;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class CraftCalio {
	public static CraftCalio INSTANCE = new CraftCalio();
	public final ConcurrentHashMap<ResourceLocation, Pair<FactoryData, Class<? extends FactoryHolder>>> types = new ConcurrentHashMap<>();
	public final ArrayList<AccessorKey> keys = new ArrayList<>();
	public final ArrayList<AssetIdentifier> assetKeys = new ArrayList<>();
	private final List<File> datapackDirectoriesToParse = new ArrayList<>();
	private final List<Future<?>> asyncTasks = new ArrayList<>();
	private boolean isDebugging;
	private Optional<ExecutorService> threadPool = Optional.empty();

	public void addDatapackPath(@NotNull Path path) {
		this.datapackDirectoriesToParse.add(path.toFile());
	}

	public void start(boolean debug, ExecutorService service) throws TimeoutException {
		this.isDebugging = debug;
		if (service == null) {
			this.threadPool = Optional.empty();
		} else {
			this.threadPool = Optional.of(service);
		}

		long nanosStarted = Util.getNanos();
		this.debug("Starting CraftCalio parser...");
		this.assetKeys
			.stream()
			.sorted(Comparator.comparingInt(AssetIdentifier::priority))
			.forEach(
				assetKey -> this.datapackDirectoriesToParse
					.forEach(
						root -> {
							for (File datapack : root.listFiles()) {
								this.submit(
									() -> {
										try {
											FileReader reader = FileReaderFactory.createFileReader(datapack.toPath());
											if (reader == null) {
												return;
											}

											for (String file : reader.listFiles()) {
												file = file.replace("/", "\\");
												if (file.startsWith("assets\\") && file.endsWith(assetKey.fileType())) {
													String fixedJsonFile = file.substring(file.indexOf("assets\\"));
													String[] parts = fixedJsonFile.split("\\\\");
													String namespace = parts[1];
													String name = fixedJsonFile.split("\\\\")[2].split("\\\\")[0];
													String key = assetKey.directory()
														+ "/"
														+ fixedJsonFile.substring(fixedJsonFile.indexOf(namespace) + namespace.length() + 1)
														.replace("\\", "/")
														.replace(name + "/", "");
													if (assetKey.directory().equalsIgnoreCase(name)) {
														try (
															InputStream is = reader.getFileStream(file.replace("\\", "/"));
															BufferedReader br = new BufferedReader(new InputStreamReader(is))
														) {
															Class<? extends Registrable> toInvoke = assetKey.registryKey().type();
															ResourceLocation namespacedKey = ResourceLocation.fromNamespaceAndPath(namespace, key);
															switch (assetKey.assetType()) {
																case JSON:
																	StringBuilder line = new StringBuilder();

																	String newLine;
																	while ((newLine = br.readLine()) != null) {
																		line.append(newLine);
																	}

																	String finishedLine = line.toString().replace("\n", "");

																	JsonObject assetParser;
																	try {
																		assetParser = JsonParser.parseReader(new StringReader(finishedLine)).getAsJsonObject();
																	} catch (Throwable var23) {
																		this.getLogger()
																			.severe(
																				"An unhandled exception occurred when parsing a json file! Invalid syntax? The datapack will not be loaded."
																			);
																		return;
																	}

																	CalioRegistry.INSTANCE
																		.retrieve(assetKey.registryKey())
																		.register(
																			toInvoke.getConstructor(ResourceLocation.class, JsonObject.class)
																				.newInstance(namespacedKey, assetParser)
																		);
																	break;
																case IMAGE:
																	CalioRegistry.INSTANCE
																		.retrieve(assetKey.registryKey())
																		.register(
																			toInvoke.getConstructor(ResourceLocation.class, BufferedImage.class)
																				.newInstance(namespacedKey, ImageIO.read(is))
																		);
															}
														} catch (Throwable var26) {
															var26.printStackTrace();
														}
													}
												}
											}
										} catch (Throwable var27) {
											var27.printStackTrace();
										}
									}
								);
							}
						}
					)
			);
		this.keys
			.stream()
			.sorted(Comparator.comparingInt(AccessorKey::getPriority))
			.forEach(
				accessorKey -> this.datapackDirectoriesToParse
					.forEach(
						root -> {
							for (File datapack : root.listFiles()) {
								this.submit(
									() -> {
										try {
											FileReader fileReader = FileReaderFactory.createFileReader(datapack.toPath());
											if (fileReader == null) {
												return;
											}

											List<String> files = fileReader.listFiles();
											HashMap<Pair<JsonObject, ResourceLocation>, Integer> newLoadingPrioritySortedMap = new HashMap<>();

											for (String file : files) {
												file = file.replace("/", "\\");
												if (file.startsWith("data\\") && file.endsWith(".json")) {
													String fixedJsonFile = file.substring(file.indexOf("data\\"));
													String[] parts = fixedJsonFile.split("\\\\");
													String namespace = parts[1];
													String name = fixedJsonFile.split("\\\\")[2].split("\\\\")[0];
													String key = fixedJsonFile.substring(fixedJsonFile.indexOf(namespace) + namespace.length() + 1)
														.replace(".json", "")
														.replace("\\", "/")
														.replace(name + "/", "");
													if (accessorKey.getDirectory().equalsIgnoreCase(name)) {
														try (
															InputStream is = fileReader.getFileStream(file.replace("\\", "/"));
															BufferedReader br = new BufferedReader(new InputStreamReader(is))
														) {
															StringBuilder line = new StringBuilder();

															String newLine;
															while ((newLine = br.readLine()) != null) {
																line.append(newLine);
															}

															String finishedLine = line.toString().replace("\n", "");
															if (this.isCorrupted(finishedLine)) {
																this.getLogger()
																	.severe(
																		"The json file with ResourceLocation of \"{}\" is corrupted! Please contact the pack author."
																			.replace("{}", namespace + ":" + key)
																	);
															} else {
																JsonObject powerParser;
																try {
																	powerParser = JsonParser.parseReader(new StringReader(finishedLine)).getAsJsonObject();
																} catch (Throwable var23) {
																	this.getLogger()
																		.severe(
																			"An unhandled exception occurred when parsing a json file \"{}\"! Invalid syntax? The datapack will not be loaded."
																				.replace("{}", namespace + ":" + key)
																		);
																	continue;
																}

																ResourceLocation namespacedKey = ResourceLocation.fromNamespaceAndPath(namespace, key);
																JsonObject remappedJsonObject = JsonObjectRemapper.remapJsonObject(powerParser, namespacedKey);
																newLoadingPrioritySortedMap.put(
																	new Pair<>(remappedJsonObject, namespacedKey),
																	remappedJsonObject.has("loading_priority")
																		? remappedJsonObject.getAsJsonPrimitive("loading_priority").getAsInt()
																		: 0
																);
															}
														}
													}
												}
											}

											List<Entry<Pair<JsonObject, ResourceLocation>, Integer>> list = new ArrayList<>(newLoadingPrioritySortedMap.entrySet());
											Collections.sort(list, Entry.comparingByValue());

											for (Entry<Pair<JsonObject, ResourceLocation>, Integer> entry : list) {
												CalioJsonParser.init(entry.getKey(), accessorKey);
											}

											newLoadingPrioritySortedMap.clear();
										} catch (IOException var26) {
											var26.printStackTrace();
										}
									}
								);
							}
						}
					)
			);
		if (!this.asyncTasks.isEmpty()) {
			CompletableFuture<?>[] futuresArray = this.asyncTasks.toArray(new CompletableFuture[0]);

			try {
				CompletableFuture.allOf(futuresArray).get(2L, TimeUnit.MINUTES);
				this.debug("Finished calio threaded-parsing in {}ms".replace("{}", String.valueOf((Util.getNanos() - nanosStarted) / 1000000L)));
			} catch (Exception var7) {
				this.asyncTasks.forEach(future -> future.cancel(true));
				throw new TimeoutException("Waited for 2 minutes for threaded parsing tasks to be completed and they have not finished, terminating.");
			}
		}
	}

	private void submit(Runnable runnable) {
		if (this.getThreadPool().isEmpty()) {
			runnable.run();
		} else {
			this.asyncTasks.add(CompletableFuture.runAsync(runnable, this.getThreadPool().get()));
		}
	}

	private boolean isCorrupted(String finishedLine) {
		try {
			JsonElement jsonElement = JsonParser.parseString(finishedLine);
			return !jsonElement.isJsonObject();
		} catch (JsonSyntaxException var3) {
			return true;
		}
	}

	public void debug(String msg) {
		if (this.isDebugging) {
			this.getLogger().info(msg);
		}
	}

	public Logger getLogger() {
		return Logger.getLogger("CraftCalio");
	}

	public void register(Class<? extends FactoryHolder> holder) {
		try {
			if (!holder.isAnnotationPresent(DontRegister.class)) {
				if (holder.isAnnotationPresent(RequiresPlugin.class)) {
					RequiresPlugin aN = holder.getAnnotation(RequiresPlugin.class);
					if (!Bukkit.getPluginManager().isPluginEnabled(aN.pluginName())) {
						return;
					}
				}

				ClassLoader classLoader = CraftCalio.class.getClassLoader();
				classLoader.loadClass(holder.getName());
				Method rC = holder.getDeclaredMethod("registerComponents", FactoryData.class);
				if (rC == null) {
					throw new IllegalArgumentException("FactoryHolder doesn't have registerComponents method in it or its superclasses!");
				} else {
					FactoryData data = (FactoryData) rC.invoke(null, new FactoryData());
					ResourceLocation identifier = data.getIdentifier();
					if (identifier == null) {
						throw new IllegalArgumentException("Type identifier was not provided! FactoryHolder will not be loaded : " + holder.getSimpleName());
					} else {
						this.types.put(identifier, new Pair<>(data, holder));
					}
				}
			}
		} catch (ClassNotFoundException var6) {
			throw new RuntimeException("Unable to resolve class during registration!", var6);
		} catch (Throwable var7) {
			if (!(var7 instanceof NoSuchMethodException)) {
				if (var7 instanceof IllegalArgumentException) {
					this.getLogger().severe("Type provider was not provided! FactoryHolder will not be loaded. : " + holder.getSimpleName());
				}

				var7.printStackTrace();
				throw new RuntimeException("An exception occured when registering FactoryHolder", var7);
			}
		}
	}

	public <T extends Registrable> void registerAccessor(
		String directory, int priority, boolean useTypeDefiner, Class<? extends FactoryHolder> typeOf, RegistryKey<T> registryKey, String defaultType
	) {
		this.keys.add(new AccessorKey<>(directory, priority, useTypeDefiner, registryKey, typeOf, defaultType));
	}

	public <T extends Registrable> void registerAccessor(
		String directory, int priority, boolean useTypeDefiner, Class<? extends FactoryHolder> typeOf, RegistryKey<T> registryKey
	) {
		this.keys.add(new AccessorKey<>(directory, priority, useTypeDefiner, registryKey, typeOf, null));
	}

	public <T extends Registrable> void registerAsset(
		String directory, int priority, String fileType, AssetIdentifier.AssetType assetType, RegistryKey<T> registryKey
	) {
		this.assetKeys.add(new AssetIdentifier<>(directory, priority, fileType, assetType, registryKey));
	}

	public Optional<ExecutorService> getThreadPool() {
		return this.threadPool;
	}

	public void setThreadPool(Optional<ExecutorService> threadPool) {
		this.threadPool = threadPool;
	}
}
