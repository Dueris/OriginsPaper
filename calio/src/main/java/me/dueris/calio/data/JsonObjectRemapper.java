package me.dueris.calio.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.util.NamespaceUtils;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class JsonObjectRemapper {
	/**
	 * Allows telling the remapper to swap out type values
	 */
	public static final HashMap<String, String> typeAlias = new HashMap<>();
	/**
	 * Map<KEY, Pair<CURRENT -> REMAPPED>>
	 */
	private static final HashMap<String, ArrayList<Pair<Object, Object>>> objectMappings = new HashMap<>();
	/**
	 * List<Pair<CURRENT -> REMAPPED>>
	 */
	public static ArrayList<Pair<String, String>> typeMappings = new ArrayList<>();

	/**
	 * Adds a mapping for the given key and mapper.
	 *
	 * @param key    the key for the mapping
	 * @param mapper the mapper to be added
	 * @return void
	 */
	public static void addObjectMapping(String key, Pair<Object, Object> mapper) {
		if (objectMappings.containsKey(key)) {
			objectMappings.get(key).add(mapper);
		} else {
			ArrayList<Pair<Object, Object>> list = new ArrayList<>();
			list.add(mapper);
			objectMappings.put(key, list);
		}
	}

	/**
	 * Recursively remaps the keys and values of a JSON object based on predefined mappings and dynamic namespaces.
	 *
	 * @param obj              the JSON object to be remapped
	 * @param currentNamespace the current namespace used for dynamic namespace remapping
	 */
	public static JsonObject remapJsonObject(JsonObject obj, NamespacedKey currentNamespace) {
		JsonObject objectReturnable = new JsonObject();
		for (String key : obj.keySet()) {
			if (objectReturnable.has(key))
				throw new IllegalStateException("JsonFile has duplicate value: (key=\"{k}\", namespace=\"{n}\"".replace("{k}", key).replace("{n}", currentNamespace.asString()));
			JsonElement valueInst = obj.get(key);
			// Object mappings
			for (String keyName : objectMappings.keySet()) {
				if (keyName.equalsIgnoreCase(key)) {
					System.out.println(key);
					for (Pair<Object, Object> objectMapping : objectMappings.get(key)) {
						if (valueInst.equals(objectMapping.left())) {
							objectReturnable.addProperty(key, objectMapping.right().toString());
						}
					}
				}
			}
			// DynamicNamespace remapping
			if (valueInst.isJsonPrimitive() && valueInst.getAsJsonPrimitive().isString()) {
				String g = valueInst.getAsJsonPrimitive().getAsString();
				if (g.contains(":") && g.contains("*")) {
					g = NamespaceUtils.getDynamicNamespace(currentNamespace.asString(), g).asString();
				}

				for (Pair<String, String> pair : typeMappings) {
					if (key.equalsIgnoreCase("type") && g.startsWith(pair.left())) {
						g = pair.right() + ":" + g.split(":")[1];
					}
				}

				if (key.equalsIgnoreCase("type")) {
					if (typeAlias.containsKey(g)) {
						g = typeAlias.get(g);
					}
				}

				objectReturnable.addProperty(key, g);
			} else if (valueInst.isJsonObject()) {
				objectReturnable.add(key, remapJsonObject(valueInst.getAsJsonObject(), currentNamespace));
				continue;
			} else if (valueInst.isJsonArray()) {
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

			if (!objectReturnable.has(key)) objectReturnable.add(key, valueInst);
		}
		return objectReturnable;
	}

}