package io.github.dueris.calio.parser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.dueris.calio.data.AccessorKey;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.calio.registry.impl.CalioRegistry;
import io.github.dueris.calio.util.ReflectionUtils;
import io.github.dueris.calio.util.Util;
import io.github.dueris.calio.util.annotations.DontRegister;
import io.github.dueris.calio.util.annotations.RequiresPlugin;
import io.github.dueris.calio.util.annotations.SourceProvider;
import io.github.dueris.calio.util.holder.ObjectProvider;
import io.github.dueris.calio.util.holder.ObjectTiedEnumState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CalioParser {
	public static final AtomicReference<JsonObjectRemapper> REMAPPER = new AtomicReference<>();
	public static final Logger LOGGER = LogManager.getLogger("CalioParser");
	private static final Gson GSON = new Gson();
	public static ExecutorService threadedParser;
	public static boolean threaded = false;

	@SuppressWarnings("unchecked")
	public static <T> @NotNull ConcurrentLinkedQueue<Tuple<?, ResourceLocation>> fromJsonFile(Map.@NotNull Entry<AccessorKey<?>, ConcurrentLinkedQueue<Tuple<String, String>>> entry) {
		AccessorKey<?> accessorKey = entry.getKey();
		// We return the same result, but implement a check that it's valid to ensure it's ready for parsing.
		ConcurrentLinkedQueue<Tuple<SerializableData, Class<? extends T>>> typedTempInstance = new ConcurrentLinkedQueue<>();
		final Class<? extends T>[] defaultType = new Class[]{null};
		Class<T> clz = accessorKey.strategy().equals(ParsingStrategy.DEFAULT) ? (Class<T>) accessorKey.toBuild() :
			((ObjectProvider<Class<T>>) () -> {
				try {
					ConcurrentLinkedQueue<Class<? extends T>> instanceTypes = (ConcurrentLinkedQueue<Class<? extends T>>) ReflectionUtils.getStaticFieldValue(accessorKey.toBuild(), "INSTANCE_TYPES");
					for (Class<? extends T> instanceType : instanceTypes) {
						if (ReflectionUtils.hasMethod(instanceType, "buildFactory", true)) {
							typedTempInstance.add(new Tuple<>(ReflectionUtils.invokeStaticMethod(instanceType, "buildFactory"), instanceType));
						}
					}
					if (ReflectionUtils.hasField(accessorKey.toBuild(), "DEFAULT_TYPE", true)) {
						defaultType[0] = (Class<? extends T>) ReflectionUtils.getStaticFieldValue(accessorKey.toBuild(), "DEFAULT_TYPE");
					}
				} catch (Throwable throwable) {
					throw new RuntimeException("Unable to parse INSTANCE_TYPES field for class '" + accessorKey.toBuild().getSimpleName() + "'");
				}
				return (Class<T>) accessorKey.toBuild();
			}).get();
		ConcurrentLinkedQueue<Tuple<?, ResourceLocation>> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
		if (ReflectionUtils.hasAnnotation(clz, DontRegister.class)) {
			return concurrentLinkedQueue;
		}
		if (ReflectionUtils.hasAnnotation(clz, RequiresPlugin.class)) {
			String pluginInstance = ReflectionUtils.getAnnotation(clz, RequiresPlugin.class).get().pluginName();
			if (!Bukkit.getPluginManager().isPluginEnabled(pluginInstance)) {
				return concurrentLinkedQueue;
			}
		}
		if (ReflectionUtils.hasMethod(clz, "buildFactory", true)) {
			List<CompletableFuture<Void>> parsingTasks = new ArrayList<>();
			for (Tuple<String, String> Tuple : entry.getValue()) {
				Optional<CompletableFuture<Void>> future = submitParseTask(() -> {
					parseFile(new Tuple<>(Util.buildResourceLocationFromPath(Tuple.getA()), Tuple.getB()), clz, accessorKey, defaultType, typedTempInstance);
				});

				Objects.requireNonNull(parsingTasks);
				future.ifPresent(parsingTasks::add);
			}

			CompletableFuture<Void> allOf = CompletableFuture.allOf(parsingTasks.toArray(new CompletableFuture[0]));
			try {
				allOf.get();
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		} else {
			LOGGER.error("Provided class, {} has no static method 'buildFactory'", clz.getSimpleName());
		}

		return concurrentLinkedQueue;
	}

	public static <T> @Nullable T parseFile(@NotNull Tuple<ResourceLocation, String> Tuple, Class<T> clz, AccessorKey<?> accessorKey, Class<? extends T>[] defaultType, ConcurrentLinkedQueue<Tuple<SerializableData, Class<? extends T>>> typedTempInstance) {
		final AtomicBoolean[] kill = {new AtomicBoolean(false)};
		ResourceLocation location = Tuple.getA();
		String jsonContents = Tuple.getB();
		if (location == null)
			throw new RuntimeException("Unable to compile ResourceLocation for CalioParser!");
		SerializableData definer;
		Class<? extends T> toBuild = clz;

		JsonObject jsonSource = REMAPPER.get().remap(GSON.fromJson(jsonContents, JsonObject.class), location).getAsJsonObject();
		switch (accessorKey.strategy()) {
			case TYPED -> {
				Class<? extends T> typedInst;
				if (!jsonSource.has("type")) {
					if (defaultType[0] != null) {
						typedInst = defaultType[0];
					} else {
						LOGGER.error("Error when parsing {} : 'type' field is required for {} instances", location.toString(), clz.getSimpleName());
						kill[0].set(true);
						return null;
					}
				} else {
					try {
						typedInst = typedTempInstance.stream().filter(stringClassTuple -> {
							return stringClassTuple.getA().typedInstance != null && stringClassTuple.getA().typedInstance.toString().equalsIgnoreCase(jsonSource.get("type").getAsString());
						}).findFirst().get().getB();
					} catch (NoSuchElementException e) {
						kill[0].set(true);
						LOGGER.error("Unable to retrieve type instance of '{}'", jsonSource.get("type").getAsString());
						return null;
					}
				}
				if (typedInst != null) {
					toBuild = typedInst;
				}
			}
		}
		if (toBuild == null)
			throw new RuntimeException("Unable to parse type for class '" + clz.getSimpleName() + "' and type value of '" + jsonSource.get("type").getAsString() + "'");
		try {
			if (!ReflectionUtils.hasMethod(toBuild, "buildFactory", true))
				throw new IllegalArgumentException("Class '" + toBuild.getSimpleName() + "' must have the method 'buildFactory' but one was not found!");
			definer = ReflectionUtils.invokeStaticMethod(toBuild, "buildFactory");
		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		Optional<Tuple<List<Tuple<String, ?>>, List<Tuple<String, ?>>>> compiledInstance = compileFromInstanceDefinition(definer, jsonSource, Optional.of(location.toString()), Optional.of(clz));
		if (compiledInstance.isEmpty()) return null;
		List<Tuple<String, ?>> compiledArguments = compiledInstance.get().getB();
		List<Tuple<String, ?>> compiledParams = compiledInstance.get().getA();

		if (kill[0].get()) return null;

		List<Class<?>> parameterTypes = (List<Class<?>>) definer.sortByPriorities(compiledParams);
		parameterTypes.addFirst(ResourceLocation.class);

		Constructor<?> constructor;
		try {
			constructor = toBuild.getConstructor(parameterTypes.toArray(new Class[0]));
		} catch (NoSuchMethodException e) {
			LOGGER.error("No such constructor with the given parameter types: {}", e.getMessage());
			e.printStackTrace();
			return null;
		}

		List arguments = definer.sortByPriorities(compiledArguments);
		arguments.addFirst(location);

		Object[] argsArray = new Object[parameterTypes.size()];
		for (int i = 0; i < parameterTypes.size(); i++) {
			Class<?> paramType = parameterTypes.get(i);
			Object arg = (i < arguments.size()) ? arguments.get(i) : null;

			if (arg != null && !paramType.isInstance(arg)) {
				try {
					arg = convertArgument(arg, paramType);
				} catch (Exception e) {
					LOGGER.error("Error converting argument {} to type {}: {}", arg, paramType, e.getMessage());
					continue;
				}
			}

			argsArray[i] = arg;
		}
		// Post-processing start
		if (definer.postProcessor != null) {
			SerializableData.Instance factoryJson = SerializableData.Instance.decompileJsonObject(
				jsonSource, definer, toBuild.getSimpleName(), location.toString(), Optional.of(toBuild)
			);
			definer.postProcessor.accept(factoryJson);
		}

		try {
			return finalizeInstance((T) constructor.newInstance(argsArray), jsonSource, accessorKey, location);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
				 InvocationTargetException e) {
			LOGGER.error("Error compiling instanceof {} : {}", toBuild.getSimpleName(), e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static <T> @NotNull T finalizeInstance(@NotNull T instance, JsonObject jsonSource, AccessorKey<?> accessorKey, ResourceLocation location) {
		if (ReflectionUtils.hasFieldWithAnnotation(instance.getClass(), JsonObject.class, SourceProvider.class)) {
			ReflectionUtils.setFieldWithAnnotation(instance, SourceProvider.class, jsonSource);
		}
		RegistryKey<T> registryKey = (RegistryKey<T>) accessorKey.registryKey();
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

	public static <T> Optional<Tuple<List<Tuple<String, ?>>, List<Tuple<String, ?>>>> compileFromInstanceDefinition(@NotNull SerializableData definer, JsonObject jsonSource, Optional<String> location, Optional<Class<T>> clz) {
		List<Tuple<String, ?>> compiledParams = new ArrayList<>();
		List<Tuple<String, ?>> compiledArguments = new ArrayList<>();
		for (Map.Entry<String, ObjectTiedEnumState<SerializableDataBuilder<?>>> entry : definer.dataMap().entrySet()) {
			String key = entry.getKey();
			ObjectTiedEnumState<SerializableDataBuilder<?>> serializableTiedBoolean = entry.getValue();
			SerializableType type = (SerializableType) serializableTiedBoolean.state();
			try {
				switch (type) {
					case DEFAULT:
						compiledParams.add(new Tuple<>(key, serializableTiedBoolean.object().type()));
						if (jsonSource.has(key)) {
							compiledArguments.add(new Tuple<>(key, serializableTiedBoolean.object().deserialize(jsonSource.get(key))));
						} else {
							if (!definer.defaultMap.containsKey(key)) {
								throw new UnsupportedOperationException("A default value was provided but it wasn't fetch-able by the calio compiler!");
							}

							compiledArguments.add(new Tuple<>(key, definer.defaultMap.get(key)));
						}
						break;
					case REQUIRED:
						compiledParams.add(new Tuple<>(key, serializableTiedBoolean.object().type()));
						if (jsonSource.has(key)) {
							compiledArguments.add(new Tuple<>(key, serializableTiedBoolean.object().deserialize(jsonSource.get(key))));
						} else {
							LOGGER.error("Required instance not found, skipping instance compiling for '{}' : KEY ['{}'] | ClassName [{}]", location.orElse("Unknown Key"), key, clz.isPresent() ? clz.get() : "Unknown Class");
							LOGGER.error("JSON: {}", jsonSource.get(key).toString());
							return Optional.empty();
						}
				}
			} catch (Throwable throwable) {
				LOGGER.error("Unable to compile '{}' from InstanceDefinition: '{}'", serializableTiedBoolean.object() == null ? "NULL" : serializableTiedBoolean.object().asString(), location.orElse("no_location_found"));
				LOGGER.error("JSON-ELEMENT: {}", jsonSource.get(key).toString());
				throwable.printStackTrace();
			}
		}
		if (!compiledParams.isEmpty() && !compiledArguments.isEmpty()) {
			return Optional.of(new Tuple<>(compiledParams, compiledArguments));
		}
		return Optional.empty();
	}

	private static Object convertArgument(Object arg, @NotNull Class<?> paramType) {
		if (paramType.isPrimitive()) {
			return convertToPrimitive(arg, paramType);
		} else if (isWrapperType(paramType)) {
			return convertToWrapper(arg, paramType);
		}
		throw new IllegalArgumentException("Unsupported type conversion for: " + paramType.getName());
	}

	private static boolean isWrapperType(Class<?> type) {
		return type == Integer.class || type == Boolean.class || type == Double.class || type == Float.class ||
			type == Long.class || type == Short.class || type == Byte.class || type == Character.class;
	}

	private static Object convertToPrimitive(Object arg, Class<?> primitiveType) {
		if (primitiveType == int.class) return ((Number) arg).intValue();
		if (primitiveType == boolean.class) return arg;
		if (primitiveType == double.class) return ((Number) arg).doubleValue();
		if (primitiveType == float.class) return ((Number) arg).floatValue();
		if (primitiveType == long.class) return ((Number) arg).longValue();
		if (primitiveType == short.class) return ((Number) arg).shortValue();
		if (primitiveType == byte.class) return ((Number) arg).byteValue();
		if (primitiveType == char.class) return arg.toString().charAt(0);
		throw new IllegalArgumentException("Unsupported primitive type: " + primitiveType.getName());
	}

	private static @Unmodifiable Object convertToWrapper(Object arg, Class<?> wrapperType) {
		if (wrapperType == Integer.class) return Integer.valueOf(((Number) arg).intValue());
		if (wrapperType == Boolean.class) return arg;
		if (wrapperType == Double.class) return Double.valueOf(((Number) arg).doubleValue());
		if (wrapperType == Float.class) return Float.valueOf(((Number) arg).floatValue());
		if (wrapperType == Long.class) return Long.valueOf(((Number) arg).longValue());
		if (wrapperType == Short.class) return Short.valueOf(((Number) arg).shortValue());
		if (wrapperType == Byte.class) return Byte.valueOf(((Number) arg).byteValue());
		if (wrapperType == Character.class) return Character.valueOf(arg.toString().charAt(0));
		throw new IllegalArgumentException("Unsupported wrapper type: " + wrapperType.getName());
	}

}
