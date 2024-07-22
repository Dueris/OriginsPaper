package me.dueris.calio.parse;

import com.google.gson.JsonObject;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.data.*;
import me.dueris.calio.data.annotations.RequiresPlugin;
import me.dueris.calio.registry.impl.CalioRegistry;
import me.dueris.calio.util.holders.Pair;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CalioJsonParser {

	public static @Nullable FactoryHolder init(@NotNull Pair<JsonObject, ResourceLocation> pair, @NotNull AccessorKey accessorKey) {
		ResourceLocation key = pair.second();
		if (accessorKey.getOfType() == null) {
			return null;
		} else {
			try {
				FactoryData data;
				Class<? extends FactoryHolder> holder;
				if (accessorKey.usesTypeDefiner()) {
					String type = pair.first().has("type") ? pair.first().get("type").getAsString() : accessorKey.getDefaultType();
					if (!CraftCalio.INSTANCE.types.containsKey(ResourceLocation.parse(type))) {
						CraftCalio.INSTANCE
							.getLogger()
							.severe(
								"Unknown type was provided! : {a} | {b}"
									.replace("{a}", ResourceLocation.parse(pair.first().get("type").getAsString()).toString())
									.replace("{b}", key.toString())
							);
						return null;
					}

					data = CraftCalio.INSTANCE.types.get(ResourceLocation.parse(type)).first();
					holder = CraftCalio.INSTANCE.types.get(ResourceLocation.parse(type)).second();
				} else {
					holder = accessorKey.getOfType();
					Method rC = holder.getDeclaredMethod("registerComponents", FactoryData.class);
					if (rC == null) {
						throw new IllegalArgumentException("FactoryHolder doesn't have registerComponents method in it or its superclasses!");
					}

					if (holder.isAnnotationPresent(RequiresPlugin.class)) {
						RequiresPlugin aN = holder.getAnnotation(RequiresPlugin.class);
						if (!Bukkit.getPluginManager().isPluginEnabled(aN.pluginName())) {
							return null;
						}
					}

					data = (FactoryData) rC.invoke(null, new FactoryData());
				}

				Constructor<? extends FactoryHolder> constructor = findConstructor(data, holder);
				if (constructor == null) {
					throw new IllegalStateException("Unable to find constructor for provided type!");
				}

				FactoryHolder created = ConstructorCreator.invoke(constructor, data, pair);
				if (created != null) {
					created.ofResourceLocation(pair.second());
					if (created.canRegister()) {
						CalioRegistry.INSTANCE.<FactoryHolder>retrieve(accessorKey.getRegistryKey()).registerOrThrow(created);
						created.bootstrap();
					}

					return created;
				}
			} catch (Throwable var7) {
				var7.printStackTrace();
				String[] stacktrace = new String[]{"\n"};
				Arrays.stream(var7.getStackTrace()).map(StackTraceElement::toString).forEach(string -> stacktrace[0] = stacktrace[0] + "\tat " + string + "\n");
				CraftCalio.INSTANCE.getLogger().severe("An unhandled exception was thrown when attempting to create new Registerable!");
				CraftCalio.INSTANCE
					.getLogger()
					.severe(
						"Registry: {a} | Associated Namespace: {b} | Type: {c} | Throwable: {d}"
							.replace("{a}", accessorKey.getOfType().getSimpleName())
							.replace("{b}", key.toString())
							.replace("{c}", accessorKey.usesTypeDefiner() ? pair.first().get("type").getAsString() : "No Type")
							.replace("{d}", var7.getMessage() == null ? "Null Message" : var7.getMessage())
							+ stacktrace[0]
					);
			}

			return null;
		}
	}

	private static @Nullable Constructor<? extends FactoryHolder> findConstructor(@NotNull FactoryData data, @NotNull Class<? extends FactoryHolder> holder) {
		Class<?>[] params = Arrays.stream(data.getProviders()).map(FactoryDataDefiner::getType).toList().toArray(new Class[0]);

		try {
			return holder.getConstructor(params);
		} catch (NoSuchMethodException var4) {
			var4.printStackTrace();
			return null;
		}
	}
}
