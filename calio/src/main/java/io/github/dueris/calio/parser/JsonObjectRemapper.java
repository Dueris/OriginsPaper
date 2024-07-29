package io.github.dueris.calio.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.dueris.calio.util.holder.Pair;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class JsonObjectRemapper {
	private final List<Pair<String, String>> namespaceRemappings;
	private final List<Pair<String, String>> typeAliases;

	public JsonObjectRemapper(List<Pair<String, String>> namespaceRemappings, List<Pair<String, String>> typeAliases) {
		this.namespaceRemappings = namespaceRemappings;
		this.typeAliases = typeAliases;
	}

	public JsonElement remap(@NotNull JsonElement element, ResourceLocation currentNamespace) {
		if (element.isJsonObject()) {
			return remapJsonObject(element.getAsJsonObject(), currentNamespace);
		} else if (element.isJsonArray()) {
			return remapJsonArray(element.getAsJsonArray(), currentNamespace);
		}
		return element;
	}

	private @NotNull JsonObject remapJsonObject(@NotNull JsonObject jsonObject, ResourceLocation currentNamespace) {
		JsonObject remappedObject = new JsonObject();

		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();

			if (key.equals("type") && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
				String typeValue = value.getAsString();
				String remappedTypeValue = remapTypeValue(typeValue,currentNamespace);
				remappedObject.addProperty(key, remappedTypeValue);
			} else {
				remappedObject.add(key, remap(value, currentNamespace));
			}
		}

		return remappedObject;
	}

	private @NotNull JsonArray remapJsonArray(@NotNull JsonArray jsonArray, ResourceLocation currentNamespace) {
		JsonArray remappedArray = new JsonArray();

		for (JsonElement element : jsonArray) {
			remappedArray.add(remap(element, currentNamespace));
		}

		return remappedArray;
	}

	private String remapTypeValue(String typeValue, ResourceLocation currentNamespace) {
		if (typeValue.contains("*")) {
			if (typeValue.contains(":")) {
				String[] namespacepath = typeValue.split(":");
				typeValue = (namespacepath[0].contains("*") ? namespacepath[0].replace("*", currentNamespace.getNamespace()) : namespacepath[0]) +
					(namespacepath[1].contains("*") ? namespacepath[1].replace("*", currentNamespace.getPath()) : namespacepath[1]);
			} else {
				typeValue = currentNamespace.toString();
			}
			typeValue = typeValue.replace("*", currentNamespace.toString());
		}
		for (Pair<String, String> alias : typeAliases) {
			if (alias.first().equals(typeValue)) {
				return alias.second();
			}
		}

		String[] parts = typeValue.split(":");
		if (parts.length == 2) {
			String namespace = parts[0];
			String key = parts[1];
			for (Pair<String, String> namespaceMapping : namespaceRemappings) {
				if (namespaceMapping.first().equals(namespace)) {
					return namespaceMapping.second() + ":" + key;
				}
			}
		}

		return typeValue;
	}
}
