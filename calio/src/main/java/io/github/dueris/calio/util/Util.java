package io.github.dueris.calio.util;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.dueris.calio.CraftCalio;
import io.github.dueris.calio.data.DataBuildDirective;
import io.github.dueris.calio.mixin.HolderLookupAdapterAccessor;
import io.github.dueris.calio.mixin.RegistryOpsAccessor;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class Util {
	public static boolean pathMatchesAccessor(@NotNull String path, DataBuildDirective<?> dataBuildDirective) {
		if (!path.contains("data/")) return false;
		if (!dataBuildDirective.modids().isEmpty()) {
			try {
				String[] parts = path.split("/");
				if (parts.length < 4) return false;
				String modid = parts[2];
				String typeFolder = parts[3];
				return dataBuildDirective.folder().equalsIgnoreCase(typeFolder) && dataBuildDirective.modids().contains(modid);
			} catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}
		} else {
			try {
				String[] parts = path.split("/");
				if (parts.length < 3) return false;
				String typeFolder = parts[2];
				return dataBuildDirective.folder().equalsIgnoreCase(typeFolder);
			} catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}
		}
	}

	public static @Nullable ResourceLocation buildResourceLocationFromPath(@NotNull String path, DataBuildDirective<?> dataBuildDirective) {
		if (!path.contains("data/")) return null;
		if (!dataBuildDirective.modids().isEmpty()) {
			try {
				String[] parts = path.split("/");
				if (parts.length < 4) return null;

				StringBuilder nameBuilder = new StringBuilder(parts[4].replace(".json", ""));
				for (int i = 5; i < parts.length; i++) {
					nameBuilder.append('/').append(parts[i].replace(".json", ""));
				}
				String name = nameBuilder.toString();
				String resourceString = parts[1] + ":" + name;
				return ResourceLocation.read(resourceString).getOrThrow();
			} catch (Exception e) {
				return null;
			}
		} else {
			try {
				String[] parts = path.split("/");
				if (parts.length < 3) return null;

				StringBuilder nameBuilder = new StringBuilder(parts[3].replace(".json", ""));
				for (int i = 5; i < parts.length; i++) {
					nameBuilder.append('/').append(parts[i].replace(".json", ""));
				}
				String name = nameBuilder.toString();
				String resourceString = parts[1] + ":" + name;
				return ResourceLocation.read(resourceString).getOrThrow();
			} catch (Exception e) {
				return null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> castClass(Class<?> aClass) {
		return (Class<T>) aClass;
	}

	public static <I> I getOrDefault(@NotNull MapLike<I> mapLike, String key, I defaultValue) {
		I value = mapLike.get(key);
		return value != null ? value : defaultValue;
	}

	public static <I> Optional<RegistryOps<I>> getOrCreate(DynamicOps<I> ops) {

		if (ops instanceof RegistryOps<I> regOps) {
			return Optional.of(regOps);
		} else {
			return CraftCalio.getDynamicRegistries().map(drm -> drm.createSerializationContext(ops));
		}

	}

	public static <I> Optional<HolderLookup.Provider> getWrapperLookup(DynamicOps<I> ops) {
		return CraftCalio.getDynamicRegistries()
			.map(HolderLookup.Provider.class::cast)
			.or(() -> {

				RegistryOps.RegistryInfoLookup infoGetter = getOrCreate(ops)
					.map(RegistryOpsAccessor.class::cast)
					.map(RegistryOpsAccessor::getLookupProvider)
					.orElse(null);

				return infoGetter instanceof HolderLookupAdapterAccessor cachedInfoGetter
					? Optional.of(cachedInfoGetter.getLookupProvider())
					: Optional.empty();

			});
	}

	public static <T, I> Optional<HolderGetter<T>> getEntryLookup(DynamicOps<I> ops, ResourceKey<? extends Registry<T>> registryRef) {
		return CraftCalio.getDynamicRegistries()
			.flatMap(registries -> registries.lookup(registryRef))
			.map(impl -> (HolderGetter<T>) impl)
			.or(() -> getOrCreate(ops)
				.flatMap(registryOps -> registryOps.getter(registryRef)));
	}

	public static <T extends Enum<T>> @NotNull HashMap<String, T> buildEnumMap(@NotNull Class<T> enumClass, Function<T, String> enumToString) {
		HashMap<String, T> map = new HashMap<>();
		for (T enumConstant : enumClass.getEnumConstants()) {
			map.put(enumToString.apply(enumConstant), enumConstant);
		}
		return map;
	}

}
