package io.github.dueris.calio;

import io.github.dueris.calio.data.DataBuildDirective;
import io.github.dueris.calio.parser.CalioParser;
import io.github.dueris.calio.parser.reader.system.FileSystemReader;
import io.github.dueris.calio.util.Util;
import io.github.dueris.calio.util.thread.ParserFactory;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
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
	private static RegistryAccess registryAccess;

	/**
	 * Creates the calio parser instance and allows for providing
	 * arguments like {@code async} to make it run threaded parsing.
	 */
	public static @NotNull CraftCalio buildInstance() {
		int threadCount = 4;
		CalioParser.threadedParser = Executors.newFixedThreadPool(threadCount, new ParserFactory(threadCount));
		CalioParser.threaded = true;
		return new CraftCalio(true, threadCount);
	}

	public static void setRegistryAccess(RegistryAccess access) {
		registryAccess = access;
	}

	public static RegistryAccess registryAccess() {
		return registryAccess;
	}

	/**
	 * Creates a new builder for calio parsing to add {@link DataBuildDirective}s
	 */
	@Contract(value = " -> new", pure = true)
	public @NotNull CalioParserBuilder startBuilder() {
		return new CalioParserBuilder(this);
	}

	/**
	 * Parses the datapack path provided to build registries
	 * Example: {@code /world/datapacks/example/}
	 *
	 * @param pathToParse The datapack path to parse
	 */
	@SuppressWarnings("unchecked")
	public <T> boolean parse(@NotNull Path pathToParse) {
		Object2ObjectLinkedOpenHashMap<DataBuildDirective<T>, List<Tuple<ResourceLocation, String>>> priorityParsingQueue = new Object2ObjectLinkedOpenHashMap<>();
		CalioParserBuilder.DATA_BUILD_DIRECTIVES.forEach((key) -> {
			priorityParsingQueue.put((DataBuildDirective<T>) key, new CopyOnWriteArrayList<>());
		});
		BiConsumer<String, String> jsonVerificationFilter = (path, jsonContents) -> {

			for (DataBuildDirective<?> key : CalioParserBuilder.DATA_BUILD_DIRECTIVES) {
				if (jsonContents != null && path != null && Util.pathMatchesAccessor(path, key)) {
					priorityParsingQueue.get(key).add(new Tuple<>(Objects.requireNonNull(Util.buildResourceLocationFromPath(path, key)), jsonContents));
				}
			}

		};
		try {
			FileSystemReader.processDatapacks(pathToParse, jsonVerificationFilter);
		} catch (IOException e) {
			throw new RuntimeException("Unable to process datapacks to parse", e);
		}

		for (Map.Entry<DataBuildDirective<T>, List<Tuple<ResourceLocation, String>>> entry : new LinkedList<>(priorityParsingQueue.object2ObjectEntrySet()).stream().sorted(Comparator.comparingInt((ent) -> ent.getKey().priority())).toList()) {
			try {
				CalioParser.parseFiles(entry);
			} catch (Throwable throwable) {
				log.info("An unexpected exception occurred when building instances of {}, {}", entry.getKey().builder().type().getSimpleName(), throwable);
				throwable.printStackTrace();
			}
		}
		return true;
	}

	public void shutdown() {
		if (CalioParser.threadedParser != null) {
			CalioParser.threadedParser.shutdown();
		}
	}

	@Override
	public @NotNull String toString() {
		return "CraftCalio[" +
			"async=" + threaded + ", " +
			"threadCount=" + threadCount + ']';
	}

}
