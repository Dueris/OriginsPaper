package io.github.dueris.calio.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class JsonObjectRemapper {
	/**
	 * For hooking into where type aliases get checked and then returned
	 * The Tuple provided will be <ORIGINAL, FILE_TAG>
	 */
	public static List<Tuple<String, Consumer<Tuple<String, ResourceLocation>>>> PRE_REMAP_HOOK = new LinkedList<>();
	private final List<Tuple<String, String>> namespaceRemappings;
	private final List<Tuple<String, String>> typeAliases;
	private final List<String> aliasKeys;

	public JsonObjectRemapper(List<Tuple<String, String>> namespaceRemappings, List<Tuple<String, String>> typeAliases, List<String> aliasKeys) {
		this.namespaceRemappings = namespaceRemappings;
		this.typeAliases = typeAliases;
		this.aliasKeys = aliasKeys;
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

			if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
				String typeValue = value.getAsString();
				String remappedTypeValue = remapTypeValue(typeValue, currentNamespace, aliasKeys.contains(key));
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

	private @NotNull String remapTypeValue(@NotNull String typeValue, ResourceLocation currentNamespace, boolean aliases) {
		if (typeValue.contains("*")) {
			if (typeValue.contains(":")) {
				String[] namespacepath = typeValue.split(":");
				typeValue = (namespacepath[0].contains("*") ? namespacepath[0].replace("*", currentNamespace.getNamespace()) : namespacepath[0]) + ":" +
					(namespacepath[1].contains("*") ? namespacepath[1].replace("*", currentNamespace.getPath()) : namespacepath[1]);
			} else {
				typeValue = currentNamespace.toString();
			}
			typeValue = typeValue.replace("*", currentNamespace.toString());
		}

		if (aliases) {
			String[] parts = typeValue.split(":");
			if (parts.length == 2) {
				String namespace = parts[0];
				String key = parts[1];
				for (Tuple<String, String> namespaceMapping : namespaceRemappings) {
					if (namespaceMapping.getA().equals(namespace)) {
						typeValue = namespaceMapping.getB() + ":" + key;
						break;
					}
				}
			}

			for (Tuple<String, String> alias : typeAliases) {
				if (alias.getA().equals(typeValue)) {
					if (PRE_REMAP_HOOK.stream().map(Tuple::getA).toList().contains(typeValue)) {
						@NotNull String finalTypeValue = typeValue;
						PRE_REMAP_HOOK.stream().filter(tuple -> {
							return tuple.getA().equalsIgnoreCase(finalTypeValue);
						}).findFirst().get().getB().accept(new Tuple<>(finalTypeValue, currentNamespace));
					}
					return alias.getB();
				}
			}
		}

		return typeValue;
	}
}
