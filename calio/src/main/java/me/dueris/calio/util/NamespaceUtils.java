package me.dueris.calio.util;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class NamespaceUtils {
	public static ResourceLocation getDynamicNamespace(String currentNamespace, String dynamicNamespace) {
		if (currentNamespace == null) {
			return ResourceLocation.parse(dynamicNamespace);
		} else if (dynamicNamespace.contains("*") && currentNamespace.contains(":")) {
			String[] split = dynamicNamespace.split(":");
			String namespace = split[0];
			String path = split[1];
			if (path.contains("*")) {
				path = path.replace("*", currentNamespace.split(":")[1]);
			}

			if (namespace.contains("*")) {
				namespace = namespace.replace("*", currentNamespace.split(":")[0]);
			}

			return getNamespaceStringAsBukkit(namespace + ":" + path);
		} else {
			return ResourceLocation.parse(dynamicNamespace);
		}
	}

	public static @NotNull ResourceLocation getNamespaceStringAsBukkit(String key) {
		return ResourceLocation.parse(key);
	}
}
