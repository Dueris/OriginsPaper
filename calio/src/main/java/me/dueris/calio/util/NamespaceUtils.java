package me.dueris.calio.util;


import net.minecraft.resources.ResourceLocation;

public class NamespaceUtils {

	/**
	 * Returns a NamespacedKey object based on the given currentNamespace and dynamicNamespace.
	 * If the dynamicNamespace contains "*", it is replaced with the corresponding part of the currentNamespace.
	 * If the resulting dynamicNamespace contains ":", it is split into namespace and path.
	 * If the path contains "*", it is replaced with the corresponding part of the currentNamespace.
	 * If the namespace contains "*", it is replaced with the corresponding part of the currentNamespace.
	 *
	 * @param currentNamespace the current namespace as a string
	 * @param dynamicNamespace the dynamic namespace as a string
	 * @return a NamespacedKey object representing the dynamic namespace
	 */
	public static ResourceLocation getDynamicNamespace(String currentNamespace, String dynamicNamespace) {
		if (currentNamespace == null) return ResourceLocation.parse(dynamicNamespace);
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
		return ResourceLocation.parse(dynamicNamespace);
	}

	/**
	 * Converts a string key into a NamespacedKey object using Bukkit's NamespacedKey class.
	 *
	 * @param key the string key to be converted
	 * @return the NamespacedKey object created from the string key
	 */
	public static ResourceLocation getNamespaceStringAsBukkit(String key) {
		return ResourceLocation.parse(key);
	}
}