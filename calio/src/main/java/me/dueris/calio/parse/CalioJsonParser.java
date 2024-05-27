package me.dueris.calio.parse;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.data.*;
import me.dueris.calio.data.annotations.RequiresPlugin;
import me.dueris.calio.registry.impl.CalioRegistry;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CalioJsonParser {

	public static FactoryHolder initilize(Pair<JsonObject, NamespacedKey> pair, AccessorKey accessorKey) {
		NamespacedKey key = pair.getSecond();
		if (accessorKey.getOfType() == null) return null;
		try {
			FactoryData data;
			Class<? extends FactoryHolder> holder;
			if (accessorKey.usesTypeDefiner()) {
				String type = pair.getFirst().has("type") ? pair.getFirst().get("type").getAsString() : accessorKey.getDefaultType();
				if (!CraftCalio.INSTANCE.types.containsKey(NamespacedKey.fromString(type))) {
					CraftCalio.INSTANCE.getLogger().severe("Unknown type was provided! : {a} | {b}"
						.replace("{a}", NamespacedKey.fromString(pair.getFirst().get("type").getAsString()).asString())
						.replace("{b}", key.asString())
					);
					return null;
				} else {
					data = CraftCalio.INSTANCE.types.get(NamespacedKey.fromString(type)).getFirst();
					holder = CraftCalio.INSTANCE.types.get(NamespacedKey.fromString(type)).getSecond();
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
				if (created != null) {
					created.ofResourceLocation(pair.getSecond());
					if (created.canRegister()) {
						CalioRegistry.INSTANCE.retrieve(accessorKey.getRegistryKey()).registerOrThrow(created);
						created.bootstrap();
					}
					return created;
				}
			} else throw new IllegalStateException("Unable to find constructor for provided type!");
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			String[] stacktrace = {"\n"};
			Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).forEach(string -> stacktrace[0] += ("\tat " + string + "\n"));
			CraftCalio.INSTANCE.getLogger().severe(
				"An unhandled exception was thrown when attempting to create new Registerable!");
			CraftCalio.INSTANCE.getLogger().severe(
				"Registry: {a} | Associated Namespace: {b} | Type: {c} | Throwable: {d}"
					.replace("{a}", accessorKey.getOfType().getSimpleName())
					.replace("{b}", key.asString())
					.replace("{c}", accessorKey.usesTypeDefiner() ? pair.getFirst().get("type").getAsString() : "No Type")
					.replace("{d}", throwable.getMessage() == null ? "Null Message" : throwable.getMessage()) + stacktrace[0]
			);
		}
		return null;
	}

	private static Constructor<? extends FactoryHolder> findConstructor(FactoryData data, Class<? extends FactoryHolder> holder) {
		Class<?>[] params = Arrays.stream(data.getProviders())
			.map(FactoryDataDefiner::getType)
			.toList()
			.toArray(new Class<?>[0]);

		try {
			return holder.getConstructor(params);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}


}
