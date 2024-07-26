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
import io.github.dueris.calio.util.holder.ObjectTiedBoolean;
import io.github.dueris.calio.util.holder.Pair;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CalioParser {
	public static final AtomicReference<JsonObjectRemapper> REMAPPER = new AtomicReference<>();
	private static final Logger LOGGER = LogManager.getLogger("CraftCalioParser");
	private static final Gson GSON = new Gson();

	@SuppressWarnings("unchecked")
	public static <T> @NotNull ConcurrentLinkedQueue<Pair<?, ResourceLocation>> fromJsonFile(Map.@NotNull Entry<AccessorKey<?>, ConcurrentLinkedQueue<Pair<String, String>>> entry) throws Throwable {
		AccessorKey<?> accessorKey = entry.getKey();
		// We return the same result, but implement a check that it's valid to ensure it's ready for parsing.
		ConcurrentLinkedQueue<Pair<InstanceDefiner, Class<? extends T>>> typedTempInstance = new ConcurrentLinkedQueue<>();
		Class<T> clz = accessorKey.strategy().equals(ParsingStrategy.DEFAULT) ? (Class<T>) accessorKey.toBuild() :
			((ObjectProvider<Class<T>>) () -> {
				try {
					ConcurrentLinkedQueue<Class<? extends T>> instanceTypes = (ConcurrentLinkedQueue<Class<? extends T>>) ReflectionUtils.getStaticFieldValue(accessorKey.toBuild(), "INSTANCE_TYPES");
					for (Class<? extends T> instanceType : instanceTypes) {
						if (ReflectionUtils.hasMethod(instanceType, "buildDefiner", true)) {
							typedTempInstance.add(new Pair<>(ReflectionUtils.invokeStaticMethod(instanceType, "buildDefiner"), instanceType));
						}
					}
				} catch (Throwable throwable) {
					throw new RuntimeException("Unable to parse INSTANCE_TYPES field for class '" + accessorKey.toBuild().getSimpleName() + "'");
				}
				return (Class<T>) accessorKey.toBuild();
			}).get();
		ConcurrentLinkedQueue<Pair<?, ResourceLocation>> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
		if (ReflectionUtils.hasAnnotation(clz, DontRegister.class)) {
			return concurrentLinkedQueue;
		}
		if (ReflectionUtils.hasAnnotation(clz, RequiresPlugin.class)) {
			String pluginInstance = ReflectionUtils.getAnnotation(clz, RequiresPlugin.class).get().pluginName();
			if (!Bukkit.getPluginManager().isPluginEnabled(pluginInstance)) {
				return concurrentLinkedQueue;
			}
		}
		if (ReflectionUtils.hasMethod(clz, "buildDefiner", true)) {
			for (Pair<String, String> pair : entry.getValue()) {
				final AtomicBoolean[] kill = {new AtomicBoolean(false)};
				String path = pair.first();
				String jsonContents = pair.second();
				ResourceLocation location = Util.buildResourceLocationFromPath(path);
				if (location == null) throw new RuntimeException("Unable to compile ResourceLocation for CalioParser!");
				InstanceDefiner definer;
				Class<? extends T> toBuild = clz;

				JsonObject jsonSource = REMAPPER.get().remap(GSON.fromJson(jsonContents, JsonObject.class)).getAsJsonObject();
				if (accessorKey.strategy().equals(ParsingStrategy.TYPED)) {
					Class<? extends T> typedInst;
					if (!jsonSource.has("type")) {
						LOGGER.error("Error when parsing {} : 'type' field is required for {} instances", location.toString(), clz.getSimpleName());
						continue;
					}
					try {
						typedInst = typedTempInstance.stream().filter(stringClassPair -> {
							return stringClassPair.first().typedInstance != null && stringClassPair.first().typedInstance.toString().equalsIgnoreCase(jsonSource.get("type").getAsString());
						}).findFirst().get().second();
					} catch (NoSuchElementException e) {
						throw new RuntimeException("Unable to retrieve type instance of '" + jsonSource.get("type").getAsString() + "'");
					}
					if (typedInst != null) {
						toBuild = typedInst;
					}
				}
				if (toBuild == null)
					throw new RuntimeException("Unable to parse type for class '" + clz.getSimpleName() + "' and type value of '" + jsonSource.get("type").getAsString() + "'");
				definer = ReflectionUtils.invokeStaticMethod(toBuild, "buildDefiner");
				Optional<Pair<List<Pair<String, ?>>, List<Pair<String, ?>>>> compiledInstance = compileFromInstanceDefinition(definer, jsonSource, Optional.of(location), clz);
				if (compiledInstance.isEmpty()) continue;
				List<Pair<String, ?>> compiledArguments = compiledInstance.get().second();
				List<Pair<String, ?>> compiledParams = compiledInstance.get().first();

				if (kill[0].get()) continue;

				List<? extends Class<?>> parameterTypes = (List<? extends Class<?>>) definer.sortByPriorities(compiledParams);

				Constructor<?> constructor;
				try {
					constructor = toBuild.getConstructor(parameterTypes.toArray(new Class[0]));
				} catch (NoSuchMethodException e) {
					LOGGER.error("No such constructor with the given parameter types: {}", e.getMessage());
					e.printStackTrace();
					continue;
				}

				List<?> arguments = definer.sortByPriorities(compiledArguments);

				Object[] argsArray = new Object[parameterTypes.size()];
				for (int i = 0; i < parameterTypes.size(); i++) {
					Class<?> paramType = parameterTypes.get(i);
					Object arg = (i < arguments.size()) ? arguments.get(i) : null;

					if (arg != null && !paramType.isInstance(arg)) {
						LOGGER.error("Argument {} is of type {}, expected type is {}", i, arg.getClass().getName(), paramType.getName());
						continue;
					}

					if (arg == null && paramType.isPrimitive()) {
						if (paramType == int.class) {
							arg = 0;
						} else if (paramType == boolean.class) {
							arg = false;
						} else if (paramType == double.class) {
							arg = 0.0;
						}
					}

					argsArray[i] = arg;
				}

				try {
					T instance = (T) constructor.newInstance(argsArray);
					if (ReflectionUtils.hasFieldWithAnnotation(instance.getClass(), JsonObject.class, SourceProvider.class)) {
						ReflectionUtils.setFieldWithAnnotation(instance, SourceProvider.class, jsonSource);
					}
					RegistryKey<T> registryKey = (RegistryKey<T>) accessorKey.registryKey();
					CalioRegistry.INSTANCE.retrieve(registryKey).register(instance, location);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
						 InvocationTargetException e) {
					LOGGER.error("Error compiling instance: {}", e.getMessage());
					e.printStackTrace();
				}

			}
		} else {
			LOGGER.error("Provided class, {} has no static method 'buildDefiner'", clz.getSimpleName());
		}

		return concurrentLinkedQueue;
	}

	public static <T> Optional<Pair<List<Pair<String, ?>>, List<Pair<String, ?>>>> compileFromInstanceDefinition(@NotNull InstanceDefiner definer, JsonObject jsonSource, Optional<ResourceLocation> location, Optional<Class<T>> clz) {
		List<Pair<String, ?>> compiledParams = new ArrayList<>();
		List<Pair<String, ?>> compiledArguments = new ArrayList<>();
		for (Map.Entry<String, ObjectTiedBoolean<SerializableDataBuilder<?>>> entry : definer.dataMap().entrySet()) {
			String key = entry.getKey();
			ObjectTiedBoolean<SerializableDataBuilder<?>> serializableTiedBoolean = entry.getValue();
			boolean[] boolArgs = serializableTiedBoolean.bool();
			SerializableType type = SerializableType.build(boolArgs[0], boolArgs[1]);
			switch (type) {
				case NULLABLE:
					compiledParams.add(new Pair<>(key, serializableTiedBoolean.object().type()));
					if (jsonSource.has(key)) {
						compiledArguments.add(new Pair<>(key, serializableTiedBoolean.object().deserialize(jsonSource.get(key))));
					} else {
						compiledArguments.add(new Pair<>(key, null));
					}
					break;
				case DEFAULT:
					compiledParams.add(new Pair<>(key, serializableTiedBoolean.object().type()));
					if (jsonSource.has(key)) {
						compiledArguments.add(new Pair<>(key, serializableTiedBoolean.object().deserialize(jsonSource.get(key))));
					} else {
						if (!definer.defaultMap.containsKey(key)) {
							throw new UnsupportedOperationException("A default value was provided but it wasn't fetch-able by the calio compiler!");
						}

						compiledArguments.add(new Pair<>(key, definer.defaultMap.get(key)));
					}
					break;
				case REQUIRED:
					compiledParams.add(new Pair<>(key, serializableTiedBoolean.object().type()));
					if (jsonSource.has(key)) {
						compiledArguments.add(new Pair<>(key, serializableTiedBoolean.object().deserialize(jsonSource.get(key))));
					} else {
						LOGGER.error("Required default not found, skipping instance compiling for '{}' : KEY ['{}'] | ClassName [{}]", location.isPresent() ? location.get() : "Unknown Key", key, clz.isPresent() ? clz.get() : "Unknown Class");
						return Optional.empty();
					}
			}
		}
		if (!compiledParams.isEmpty() && !compiledArguments.isEmpty()) {
			return Optional.of(new Pair<>(compiledParams, compiledArguments));
		}
		return Optional.empty();
	}

}
