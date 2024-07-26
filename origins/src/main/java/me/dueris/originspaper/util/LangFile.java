package me.dueris.originspaper.util;

import com.google.gson.JsonObject;
import me.dueris.calio.registry.Registrable;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.dueris.originspaper.OriginsPaper.LANGUAGE;

public class LangFile implements Registrable {
	public static final Map<String, Map<String, String>> langMap = new ConcurrentHashMap<>();
	private final ResourceLocation key;

	public LangFile(ResourceLocation key, JsonObject json) {
		this.key = key;
		Map<String, String> foundLang = new HashMap<>();

		for (String jsonKey : json.keySet()) {
			if (json.get(jsonKey).isJsonPrimitive() && json.get(jsonKey).getAsJsonPrimitive().isString()) {
				foundLang.put(jsonKey, json.get(jsonKey).getAsString());
			}
		}

		langMap.put(key.getPath().replace(".json", "").replace("lang/", ""), foundLang);
	}

	public static String transform(String original) {
		if (langMap.get(LANGUAGE).containsKey(original)) {
			return langMap.get(LANGUAGE).get(original);
		}

		return original;
	}

	@Override
	public ResourceLocation key() {
		return this.key;
	}
}
