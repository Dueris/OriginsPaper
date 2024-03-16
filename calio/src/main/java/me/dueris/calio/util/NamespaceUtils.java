package me.dueris.calio.util;

import org.bukkit.NamespacedKey;

public class NamespaceUtils {
	public static NamespacedKey getDynamicNamespace(String currentNamespace, String dynamicNamespace) {
		if (dynamicNamespace.contains("*") && currentNamespace.contains(":")) {
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
		}
		return NamespacedKey.fromString(dynamicNamespace);
	}

	public static NamespacedKey getNamespaceStringAsBukkit(String key) {
		return NamespacedKey.fromString(key);
	}
}