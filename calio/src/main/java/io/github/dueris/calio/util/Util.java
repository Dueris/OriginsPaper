package io.github.dueris.calio.util;

import io.github.dueris.calio.data.AccessorKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Util {
	public static boolean pathMatchesAccessor(@NotNull String path, AccessorKey<?> accessorKey) {
		if (!path.contains("data/")) return false;
		try {
			String[] parts = path.split("/");
			if (parts.length < 4) return false;
			String modid = parts[2];
			String typeFolder = parts[3];
			return accessorKey.folder().equalsIgnoreCase(typeFolder) && accessorKey.modid().contains(modid);
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	public static @Nullable ResourceLocation buildResourceLocationFromPath(@NotNull String path) {
		if (!path.contains("data/")) return null;
		try {
			String[] parts = path.split("/");
			if (parts.length < 4) return null;
			String name = parts[4].replace(".json", "");
			String resourceString = parts[2] + ":" + name;
			return ResourceLocation.read(resourceString).getOrThrow();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> castClass(Class<?> aClass) {
		return (Class<T>) aClass;
	}
}
