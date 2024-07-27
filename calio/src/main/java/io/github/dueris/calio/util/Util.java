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
		if (!path.contains("data")) return null;
		String a = path.split("data/")[1].split("/")[0] + ":" + path.split("data/")[1].split("/")[2].replace(".json", "");
		return ResourceLocation.read(a).getOrThrow();
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> castClass(Class<?> aClass) {
		return (Class<T>) aClass;
	}
}
