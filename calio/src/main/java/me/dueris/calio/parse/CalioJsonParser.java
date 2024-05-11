package me.dueris.calio.parse;

import com.google.gson.JsonObject;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.data.*;
import me.dueris.calio.data.annotations.ProvideJsonConstructor;
import me.dueris.calio.data.annotations.RequiresPlugin;
import me.dueris.calio.registry.impl.CalioRegistry;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import oshi.util.tuples.Pair;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class CalioJsonParser {
	protected static HashMap<Pair<JsonObject, NamespacedKey>, Integer> newLoadingPrioritySortedMap = new HashMap<>();

	/**
	 * Parses the given directory, remaps JSON files, validates factory, creates instances and puts them into the registry.
	 *
	 * @param directory   the directory to parse
	 * @param accessorKey the root accessor
	 * @param namespace   the namespace for the JSON files
	 * @param before      the string to prepend to the JSON file names
	 */
	public static void parsePackDirectory(File directory, AccessorKey accessorKey, String namespace, String before, boolean createInstances) {
		Arrays.stream(directory.listFiles()).toList().forEach(jsonFile -> {
			try {
				if (!jsonFile.isDirectory()) {
					NamespacedKey key = new NamespacedKey(namespace.toLowerCase(), (before + jsonFile.getName().replace(".json", "")).toLowerCase());
					JsonObject remappedJSON = JsonObjectRemapper.createRemapped(jsonFile, key);
					newLoadingPrioritySortedMap.put(new Pair<>(remappedJSON, key), remappedJSON.has("loading_priority") ? remappedJSON.getAsJsonPrimitive("loading_priority").getAsInt() : 0);
				} else {
					parsePackDirectory(jsonFile, accessorKey, namespace, jsonFile.getName() + "/", false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		if (createInstances) {
			List<Map.Entry<Pair<JsonObject, NamespacedKey>, Integer>> list = new ArrayList<>(newLoadingPrioritySortedMap.entrySet());
			Collections.sort(list, Map.Entry.comparingByValue());

			for (Map.Entry<Pair<JsonObject, NamespacedKey>, Integer> entry : list) {
				initilize(entry.getKey(), accessorKey);
			}

			newLoadingPrioritySortedMap.clear();
		}
	}

	public static FactoryHolder initilize(Pair<JsonObject, NamespacedKey> pair, AccessorKey accessorKey) {
		NamespacedKey key = pair.getB();
		if (accessorKey.getOfType() == null) return null;
		try {
			FactoryData data;
			Class<? extends FactoryHolder> holder;
			if (accessorKey.usesTypeDefiner()) {
				if (!CraftCalio.INSTANCE.types.containsKey(NamespacedKey.fromString(pair.getA().get("type").getAsString()))) {
					CraftCalio.INSTANCE.getLogger().severe("Unknown type was provided! : {a} | {b}"
						.replace("{a}", NamespacedKey.fromString(pair.getA().get("type").getAsString()).asString())
						.replace("{b}", key.asString())
					);
					return null;
				} else {
					data = CraftCalio.INSTANCE.types.get(NamespacedKey.fromString(pair.getA().get("type").getAsString())).getFirst();
					holder = CraftCalio.INSTANCE.types.get(NamespacedKey.fromString(pair.getA().get("type").getAsString())).getSecond();
				}
			} else {
				// We gotta invoke the FactoryData manually
				holder = accessorKey.getOfType();
				Method rC = holder.getDeclaredMethod("registerComponents", FactoryData.class);
				if (rC == null)
					throw new IllegalArgumentException("FactoryHolder doesn't have registerComponents method in it or its superclasses!");
				if (holder.isAnnotationPresent(RequiresPlugin.class)) {
					RequiresPlugin aN = holder.getAnnotation(RequiresPlugin.class);
					if (!org.bukkit.Bukkit.getPluginManager().isPluginEnabled(aN.pluginName())) return null;
				}
				data = (FactoryData) rC.invoke(null, new FactoryData());
			}

			// Create the constructor
			Constructor<? extends FactoryHolder> constructor = findConstructor(data, holder);
			if (constructor != null) {
				FactoryHolder created = ConstructorCreator.invoke(constructor, data, pair);
				created.ofResourceLocation(pair.getB());
				if (created.canRegister()) {
					CalioRegistry.INSTANCE.retrieve(accessorKey.getRegistryKey()).registerOrThrow(created);
					created.bootstrap();
				}
				return created;
			} else throw new IllegalStateException("Unable to find constructor for provided type!");
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			String[] stacktrace = {"\n"};
			Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).forEach(string -> stacktrace[0] += ("\tat " + string + "\n"));
			CraftCalio.INSTANCE.getLogger().severe(
				"An unhandled exception was thrown when attempting to create new Registerable!");
			CraftCalio.INSTANCE.getLogger().severe(
				"Registry: {a} | Associated Namespace: {b} | Throwable: {c}"
					.replace("{a}", accessorKey.getOfType().getSimpleName())
					.replace("{b}", key.asString())
					.replace("{c}", throwable.getMessage()) + stacktrace[0]
			);
		}
		return null;
	}

	private static Constructor<? extends FactoryHolder> findConstructor(FactoryData data, Class<? extends FactoryHolder> holder) {
		Class<?>[] params = Arrays.stream(data.getProviders())
			.map(FactoryDataDefiner::getType)
			.toList()
			.toArray(new Class<?>[0]);

		if (holder.isAnnotationPresent(ProvideJsonConstructor.class)) {
			Class<?>[] updatedParams = Arrays.copyOf(params, params.length + 1);
			updatedParams[params.length] = JsonObject.class;
			params = updatedParams;
		}

		try {
			return holder.getConstructor(params);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}


}
