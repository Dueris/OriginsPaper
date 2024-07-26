package io.github.dueris.calio;

import io.github.dueris.calio.data.AccessorKey;
import io.github.dueris.calio.parser.CalioParser;
import io.github.dueris.calio.parser.reader.FileSystemReader;
import io.github.dueris.calio.util.Util;
import io.github.dueris.calio.util.holder.Pair;
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

public record CraftCalio(boolean debug, boolean threaded, int threadCount) {
	private static ExecutorService threadedParser;

	@Contract("_ -> new")
	public static @NotNull CraftCalio buildInstance(String[] args) {
		OptionParser parser = new OptionParser();

		parser.accepts("async").withOptionalArg().ofType(Boolean.class).defaultsTo(false);
		parser.accepts("debug").withOptionalArg().ofType(Boolean.class).defaultsTo(false);

		OptionSet options = parser.parse(args);

		boolean threaded = (Boolean) options.valueOf("async");
		int threadCount = 3;
		if (threaded) {
			threadedParser = Executors.newFixedThreadPool(threadCount, new ParserFactory(threadCount));
		}
		boolean debug = (Boolean) options.valueOf("debug");
		return new CraftCalio(debug, threaded, threadCount);
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
					Object2ObjectLinkedOpenHashMap<AccessorKey<?>, ConcurrentLinkedQueue<Pair<String, String>>> priorityParsingQueue = new Object2ObjectLinkedOpenHashMap<>();
					CalioParserBuilder.accessorKeys.forEach((key) -> {
						priorityParsingQueue.put(key, new ConcurrentLinkedQueue<>());
					});
					if (datapackDirectory.listFiles() != null) {
						BiConsumer<String, String> jsonVerificationFilter = (path, jsonContents) -> {
							ObjectIterator<AccessorKey<?>> var3 = CalioParserBuilder.accessorKeys.iterator();

							while (var3.hasNext()) {
								AccessorKey<?> key = var3.next();
								if (jsonContents != null && path != null && Util.pathMatchesAccessor(path, key)) {
									priorityParsingQueue.get(key).add(new Pair(path, jsonContents));
								}
							}

						};
						FileSystemReader.processDatapacks(pathToParse, jsonVerificationFilter);
					}

					List<Map.Entry<AccessorKey<?>, ConcurrentLinkedQueue<Pair<String, String>>>> entries = new ArrayList<>(priorityParsingQueue.object2ObjectEntrySet());
					entries.sort(Comparator.comparingInt((ent) -> {
						return ent.getKey().priority();
					}));
					Iterator<Map.Entry<AccessorKey<?>, ConcurrentLinkedQueue<Pair<String, String>>>> entryIterator = entries.iterator();
					List<CompletableFuture<Void>> parsingTasks = new ArrayList<>();

					while (entryIterator.hasNext()) {
						Map.Entry<AccessorKey<?>, ConcurrentLinkedQueue<Pair<String, String>>> entry = entryIterator.next();
						Optional<CompletableFuture<Void>> future = this.submitParseTask(() -> {
							try {
								CalioParser.fromJsonFile(entry).forEach((out) -> {
									if (out.first() == null || out.second() == null) {
										throw new RuntimeException("Output instance or output ResourceLocation was null!");
									}
								});
							} catch (Throwable throwable) {
								throwable.printStackTrace();
							}

						});
						Objects.requireNonNull(parsingTasks);
						future.ifPresent(parsingTasks::add);
					}

					CompletableFuture<Void> allOf = CompletableFuture.allOf(parsingTasks.toArray(new CompletableFuture[0]));
					allOf.get();
				}

				if (threadedParser != null) {
					threadedParser.shutdown();
				}
				return true;
			}
		}
	}

	private Optional<CompletableFuture<Void>> submitParseTask(Runnable runnable) {
		CompletableFuture<Void> voidCompletableFuture = threaded ? CompletableFuture.runAsync(runnable, threadedParser) : null;
		if (voidCompletableFuture == null) {
			runnable.run();
			return Optional.empty();
		}
		return Optional.of(voidCompletableFuture);
	}


	@Override
	public String toString() {
		return "CraftCalio[" +
			"debug=" + debug + ", " +
			"threaded=" + threaded + ", " +
			"threadCount=" + threadCount + ']';
	}

}
