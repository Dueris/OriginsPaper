package io.github.dueris.calio;

import io.github.dueris.calio.data.AccessorKey;
import io.github.dueris.calio.parser.CalioParser;
import io.github.dueris.calio.parser.reader.system.FileSystemReader;
import io.github.dueris.calio.util.Util;
import net.minecraft.util.Tuple;
import io.github.dueris.calio.util.thread.ParserFactory;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public record CraftCalio(boolean threaded, int threadCount) {

	@Contract("_ -> new")
	public static @NotNull CraftCalio buildInstance(String[] args) {
		OptionParser parser = new OptionParser();

		parser.accepts("async").withOptionalArg().ofType(Boolean.class).defaultsTo(false);

		OptionSet options = parser.parse(args);

		boolean threaded = (Boolean) options.valueOf("async");
		int threadCount = 3;
		if (threaded) {
			CalioParser.threadedParser = Executors.newFixedThreadPool(threadCount, new ParserFactory(threadCount));
			CalioParser.threaded = true;
		}
		return new CraftCalio(threaded, threadCount);
	}

	@Contract(value = " -> new", pure = true)
	public @NotNull CalioParserBuilder startBuilder() {
		return new CalioParserBuilder(this);
	}


	public boolean parse() throws Throwable {
		MinecraftServer server = MinecraftServer.getServer();
		if (server == null) {
			throw new IllegalAccessException("Server instance not created yet! Please wait until MinecraftServer is spun!");
		} else {
			Path datapackDirPath = server.getWorldPath(LevelResource.DATAPACK_DIR);
			if (!datapackDirPath.toFile().exists()) {
				datapackDirPath.toFile().mkdirs();
			}

			File datapackDirectory = datapackDirPath.toFile();
			if (!datapackDirectory.isDirectory()) {
				throw new IllegalStateException("'datapack' directory is not a directory! Corrupted?");
			} else {
				for (Path pathToParse : new Path[]{Paths.get("plugins"), datapackDirPath}) {
					Object2ObjectLinkedOpenHashMap<AccessorKey<?>, ConcurrentLinkedQueue<Tuple<String, String>>> priorityParsingQueue = new Object2ObjectLinkedOpenHashMap<>();
					CalioParserBuilder.accessorKeys.forEach((key) -> {
						priorityParsingQueue.put(key, new ConcurrentLinkedQueue<>());
					});
					if (datapackDirectory.listFiles() != null) {
						BiConsumer<String, String> jsonVerificationFilter = (path, jsonContents) -> {
							ObjectIterator<AccessorKey<?>> var3 = CalioParserBuilder.accessorKeys.iterator();

							while (var3.hasNext()) {
								AccessorKey<?> key = var3.next();
								if (jsonContents != null && path != null && Util.pathMatchesAccessor(path, key)) {
									priorityParsingQueue.get(key).add(new Tuple(path, jsonContents));
								}
							}

						};
						FileSystemReader.processDatapacks(pathToParse, jsonVerificationFilter);
					}

					List<Map.Entry<AccessorKey<?>, ConcurrentLinkedQueue<Tuple<String, String>>>> entries = new ArrayList<>(priorityParsingQueue.object2ObjectEntrySet());
					entries.sort(Comparator.comparingInt((ent) -> {
						return ent.getKey().priority();
					}));
					Iterator<Map.Entry<AccessorKey<?>, ConcurrentLinkedQueue<Tuple<String, String>>>> entryIterator = entries.iterator();
					while (entryIterator.hasNext()) {
						Map.Entry<AccessorKey<?>, ConcurrentLinkedQueue<Tuple<String, String>>> entry = entryIterator.next();
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

	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "CraftCalio[" +
			"async=" + threaded + ", " +
			"threadCount=" + threadCount + ']';
	}

}
