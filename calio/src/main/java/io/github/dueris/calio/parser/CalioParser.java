package io.github.dueris.calio.parser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.dueris.calio.data.DataBuildDirective;
import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.calio.registry.impl.CalioRegistry;
import io.github.dueris.calio.util.ReflectionUtils;
import io.github.dueris.calio.util.annotations.SourceProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class CalioParser {
	public static final AtomicReference<JsonObjectRemapper> REMAPPER = new AtomicReference<>();
	public static final Logger LOGGER = LogManager.getLogger("CalioParser");
	private static final Gson GSON = new Gson();
	public static ExecutorService threadedParser;
	public static boolean threaded = false;

	/**
	 * Parses the files inside the list provided based on the {@link DataBuildDirective}.
	 * The list provided needs to be instances of a {@link Tuple} of the location and the json contents
	 * to be parsed. After 1 1/2 minutes, the parser will kill the process since realistically it shouldn't
	 * take that long...
	 */
	public static <T> void parseFiles(Map.@NotNull Entry<DataBuildDirective<T>, List<Tuple<ResourceLocation, String>>> toParse) {
		DataBuildDirective<?> dataBuildDirective = toParse.getKey();

		List<CompletableFuture<Void>> parseTasks = new CopyOnWriteArrayList<>();
		for (Tuple<ResourceLocation, String> parseData : toParse.getValue()) {
			Optional<CompletableFuture<Void>> future = submitParseTask(() -> {
				parseFile(parseData, dataBuildDirective);
			});

			future.ifPresent(parseTasks::add);
		}

		CompletableFuture<Void> allTasks = CompletableFuture.allOf(parseTasks.toArray(new CompletableFuture[0]));
		try {
			allTasks.get(90, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e);
		}

	}

	public static <T> @Nullable T parseFile(@NotNull Tuple<ResourceLocation, String> jsonData, @NotNull DataBuildDirective<T> dataBuildDirective) {
		ResourceLocation location = jsonData.getA();
		JsonObject jsonSource = REMAPPER.get().remap(GSON.fromJson(jsonData.getB(), JsonObject.class), location).getAsJsonObject();

		RootResult<T> instance = dataBuildDirective.builder().deserialize(jsonSource);
		if (instance == null) return null;
		return finalizeInstance(instance.apply(location), jsonSource, dataBuildDirective, location);
	}

	@SuppressWarnings("unchecked")
	public static <T> @NotNull T finalizeInstance(@NotNull T instance, JsonObject jsonSource, DataBuildDirective<?> dataBuildDirective, ResourceLocation location) {
		if (ReflectionUtils.hasFieldWithAnnotation(instance.getClass(), JsonObject.class, SourceProvider.class)) {
			ReflectionUtils.setFieldWithAnnotation(instance, SourceProvider.class, jsonSource);
		}
		RegistryKey<T> registryKey = (RegistryKey<T>) dataBuildDirective.registryKey();
		if (ReflectionUtils.invokeBooleanMethod(instance, "canRegister")) {
			CalioRegistry.INSTANCE.retrieve(registryKey).register(instance, location);
		}

		return instance;
	}

	private static Optional<CompletableFuture<Void>> submitParseTask(Runnable runnable) {
		CompletableFuture<Void> voidCompletableFuture = threaded ? CompletableFuture.runAsync(runnable, threadedParser) : null;
		if (voidCompletableFuture == null) {
			runnable.run();
			return Optional.empty();
		}
		return Optional.of(voidCompletableFuture);
	}

}
