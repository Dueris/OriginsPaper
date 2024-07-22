package me.dueris.calio.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dueris.calio.util.NamespaceUtils;
import me.dueris.calio.util.holders.Pair;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonObjectRemapper {
	public static final HashMap<String, String> typeAlias = new HashMap<>();
	private static final HashMap<String, ArrayList<Pair<Object, Object>>> objectMappings = new HashMap<>();
	public static ArrayList<Pair<String, String>> typeMappings = new ArrayList<>();

	public static void addObjectMapping(String key, Pair<Object, Object> mapper) {
		if (objectMappings.containsKey(key)) {
			objectMappings.get(key).add(mapper);
		} else {
			ArrayList<Pair<Object, Object>> list = new ArrayList<>();
			list.add(mapper);
			objectMappings.put(key, list);
		}
	}

	public static @NotNull JsonObject remapJsonObject(@NotNull JsonObject obj, ResourceLocation currentNamespace) {
		JsonObject objectReturnable = new JsonObject();

		for (String key : obj.keySet()) {
			if (objectReturnable.has(key)) {
				throw new IllegalStateException(
					"JsonFile has duplicate value: (key=\"{k}\", namespace=\"{n}\"".replace("{k}", key).replace("{n}", currentNamespace.toString())
				);
			}

			JsonElement valueInst = obj.get(key);

			for (String keyName : objectMappings.keySet()) {
				if (keyName.equalsIgnoreCase(key)) {
					for (Pair<Object, Object> objectMapping : objectMappings.get(key)) {
						if (valueInst.equals(objectMapping.first())) {
							objectReturnable.addProperty(key, objectMapping.second().toString());
						}
					}
				}
			}

			if (valueInst.isJsonPrimitive() && valueInst.getAsJsonPrimitive().isString()) {
				String g = valueInst.getAsJsonPrimitive().getAsString();
				if (g.contains(":") && g.contains("*")) {
					g = NamespaceUtils.getDynamicNamespace(currentNamespace.toString(), g).toString();
				}

				for (Pair<String, String> pair : typeMappings) {
					if (key.equalsIgnoreCase("type") && g.split(":")[0].equalsIgnoreCase(pair.first())) {
						g = pair.second() + ":" + g.split(":")[1];
					}
				}

				if (key.equalsIgnoreCase("type") && typeAlias.containsKey(g)) {
					g = typeAlias.get(g);
				}

				objectReturnable.addProperty(key, g);
			} else {
				if (valueInst.isJsonObject()) {
					objectReturnable.add(key, remapJsonObject(valueInst.getAsJsonObject(), currentNamespace));
					continue;
				}

				if (valueInst.isJsonArray()) {
					objectReturnable.add(key, new JsonArray());
					JsonArray array = objectReturnable.getAsJsonArray(key);

					for (Object ob : valueInst.getAsJsonArray()) {
						if (ob instanceof JsonObject j) {
							array.add(remapJsonObject(j, currentNamespace));
						} else if (ob instanceof JsonElement element) {
							array.add(element);
						}
					}
					continue;
				}
			}

			if (!objectReturnable.has(key)) {
				objectReturnable.add(key, valueInst);
			}
		}

		return objectReturnable;
	}
}
