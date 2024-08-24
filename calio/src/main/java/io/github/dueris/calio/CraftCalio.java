package io.github.dueris.calio;

import io.github.dueris.calio.data.AccessorKey;
import io.github.dueris.calio.parser.CalioParser;
import io.github.dueris.calio.parser.reader.system.FileSystemReader;
import io.github.dueris.calio.util.Util;
import io.github.dueris.calio.util.thread.ParserFactory;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * The main class for the Calio Parser, used to start the parser with specified args
 *
 * @param threaded
 * @param threadCount
 */
public record CraftCalio(boolean threaded, int threadCount) {
	private static final Logger log = LogManager.getLogger("CraftCalio");

	/**
	 * Creates the calio parser instance and allows for providing
	 * arguments like {@code async} to make it run threaded parsing.
	 */
	@Contract("_ -> new")
	public static @NotNull CraftCalio buildInstance(String[] args) {
		OptionParser parser = new OptionParser();

		parser.accepts("async").withOptionalArg().ofType(Boolean.class).defaultsTo(false);

		OptionSet options = parser.parse(args);

		boolean threaded = (Boolean) options.valueOf("async");
		int threadCount = 4;
		if (threaded) {
			CalioParser.threadedParser = Executors.newFixedThreadPool(threadCount, new ParserFactory(threadCount));
			CalioParser.threaded = true;
		}
		return new CraftCalio(threaded, threadCount);
	}

	public static <T> boolean areTagsEqual(ResourceKey<? extends Registry<T>> registryKey, TagKey<T> tag1, TagKey<T> tag2) {
		return areTagsEqual(tag1, tag2);
	}

	public static <T> boolean areTagsEqual(TagKey<T> tag1, TagKey<T> tag2) {
		return tag1 == tag2
			|| tag1 != null
			&& tag2 != null
			&& tag1.registry().equals(tag2.registry())
			&& tag1.location().equals(tag2.location());
	}

	/**
	 * Creates a new builder for calio parsing to add {@link AccessorKey}s
	 */
	@Contract(value = " -> new", pure = true)
	public @NotNull CalioParserBuilder startBuilder() {
		return new CalioParserBuilder(this);
	}

	/**
	 * Begins parsing instances defined by the {@link AccessorKey}s in the
	 * CalioParserBuilder in both the plugins directory(and sub-dirs), and the
	 * datapacks directory defined by the {@code server.properties} file.
	 *
	 * @throws Throwable
	 */
	public boolean parse() throws Throwable {
		MinecraftServer server = MinecraftServer.getServer();
		if (server == null) {
			throw new IllegalAccessException("Server instance not created yet! Please wait until MinecraftServer is spun!");
		} else {
			Bukkit.getLogger().info("Starting CraftCalio parsing with mod implementation version '{}'".replace("{}", "1.14.0-alpha.5+mc.1.21.x"));
			Path datapackDirPath = server.getWorldPath(LevelResource.DATAPACK_DIR);
			if (!datapackDirPath.toFile().exists()) {
				datapackDirPath.toFile().mkdirs();
			}

			File datapackDirectory = datapackDirPath.toFile();
			if (!datapackDirectory.isDirectory()) {
				throw new IllegalStateException("'datapacks' directory is not a directory! Corrupted?");
			} else {
				for (Path pathToParse : new Path[]{Paths.get("plugins"), datapackDirPath}) {
					Object2ObjectLinkedOpenHashMap<AccessorKey<?>, ConcurrentLinkedQueue<Tuple<String, String>>> priorityParsingQueue = new Object2ObjectLinkedOpenHashMap<>();
					CalioParserBuilder.accessorKeys.forEach((key) -> {
						priorityParsingQueue.put(key, new ConcurrentLinkedQueue<>());
					});
					if (pathToParse.toFile().listFiles() != null) {
						BiConsumer<String, String> jsonVerificationFilter = (path, jsonContents) -> {

							for (AccessorKey<?> key : CalioParserBuilder.accessorKeys) {
								if (jsonContents != null && path != null && Util.pathMatchesAccessor(path, key)) {
									priorityParsingQueue.get(key).add(new Tuple<>(path, jsonContents));
								}
							}

						};
						FileSystemReader.processDatapacks(pathToParse, jsonVerificationFilter);
					}

					List<Map.Entry<AccessorKey<?>, ConcurrentLinkedQueue<Tuple<String, String>>>> entries = new ArrayList<>(priorityParsingQueue.object2ObjectEntrySet());
					entries.sort(Comparator.comparingInt((ent) -> {
						return ent.getKey().priority();
					}));
					for (Map.Entry<AccessorKey<?>, ConcurrentLinkedQueue<Tuple<String, String>>> entry : entries) {
						try {
							CalioParser.fromJsonFile(entry).forEach((out) -> {
								if (out.getA() == null || out.getB() == null) {
									throw new RuntimeException("Output instance or output ResourceLocation was null!");
								}
							});
						} catch (Throwable throwable) {
							throwable.printStackTrace();
						}
					}

				}

				if (CalioParser.threadedParser != null) {
					CalioParser.threadedParser.shutdown();
				}
				return true;
			}
		}
	}

	@Override
	public @NotNull String toString() {
		return "CraftCalio[" +
			"async=" + threaded + ", " +
			"threadCount=" + threadCount + ']';
	}

}
