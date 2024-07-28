package io.github.dueris.calio.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.dueris.calio.util.holder.Pair;
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

	public JsonElement remap(@NotNull JsonElement element) {
		if (element.isJsonObject()) {
			return remapJsonObject(element.getAsJsonObject());
		} else if (element.isJsonArray()) {
			return remapJsonArray(element.getAsJsonArray());
		}
		return element;
	}

	private @NotNull JsonObject remapJsonObject(@NotNull JsonObject jsonObject) {
		JsonObject remappedObject = new JsonObject();

		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();

			if (key.equals("type") && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
				String typeValue = value.getAsString();
				String remappedTypeValue = remapTypeValue(typeValue);
				remappedObject.addProperty(key, remappedTypeValue);
			} else {
				remappedObject.add(key, remap(value));
			}
		}

		return remappedObject;
	}

	private @NotNull JsonArray remapJsonArray(@NotNull JsonArray jsonArray) {
		JsonArray remappedArray = new JsonArray();

		for (JsonElement element : jsonArray) {
			remappedArray.add(remap(element));
		}

		return remappedArray;
	}

	private String remapTypeValue(String typeValue) {
		// Check type aliases first
		for (Pair<String, String> alias : typeAliases) {
			if (alias.first().equals(typeValue)) {
				return alias.second();
			}
		}

		// If no type alias matches, check namespace remappings
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

		// If no remapping is needed, return the original value
		return typeValue;
	}
}
