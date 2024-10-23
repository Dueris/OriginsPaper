package io.github.dueris.calio.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import io.github.dueris.calio.data.SerializableData;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DynamicIdentifier extends ResourceLocation {

	protected DynamicIdentifier(String namespace, String path) {
		super(namespace, path);
	}

	public static @NotNull ResourceLocation of(@NotNull JsonElement jsonElement) {
		return of(jsonElement.getAsString());
	}

	public static @NotNull ResourceLocation of(String idString) {
		return of(idString, DEFAULT_NAMESPACE);
	}

	public static DataResult<ResourceLocation> ofResult(String id) {
		return ofResult(id, DEFAULT_NAMESPACE);
	}

	public static DataResult<ResourceLocation> ofResult(String id, String defaultNamespace) {

		try {
			return DataResult.success(of(id, defaultNamespace));
		} catch (Exception e) {
			return DataResult.error(e::getMessage);
		}

	}

	public static @NotNull ResourceLocation of(String idString, String defaultNamespace) {

		String[] namespaceAndPath = splitWithNamespace(idString, defaultNamespace);
		if (namespaceAndPath[0].contains("*")) {
			if (SerializableData.CURRENT_NAMESPACE != null) {
				namespaceAndPath[0] = namespaceAndPath[0].replace("*", SerializableData.CURRENT_NAMESPACE);
			} else {
				throw new ResourceLocationException("Identifiers may only contain '*' in its namespace in data loaders that support it.");
			}
		}

		if (namespaceAndPath[1].contains("*")) {
			if (SerializableData.CURRENT_PATH != null) {
				namespaceAndPath[1] = namespaceAndPath[1].replace("*", SerializableData.CURRENT_PATH);
			} else {
				throw new ResourceLocationException("Identifiers may only contain '*' in its path in data loaders that support it.");
			}
		}

		return ResourceLocation.fromNamespaceAndPath(namespaceAndPath[0], namespaceAndPath[1]);

	}

	public static String @NotNull [] splitWithNamespace(@NotNull String idString, String defaultNamespace) {

		String[] namespaceAndPath = idString.split(String.valueOf(NAMESPACE_SEPARATOR));
		if (namespaceAndPath.length > 2) {
			throw new ResourceLocationException("Identifier \"" + idString + "\" must only have one \":\" separating its namespace and path.");
		}

		if (namespaceAndPath.length == 1) {
			return new String[]{defaultNamespace, namespaceAndPath[0]};
		}

		return namespaceAndPath;

	}
}
