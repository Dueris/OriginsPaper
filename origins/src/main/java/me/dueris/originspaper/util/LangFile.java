package me.dueris.originspaper.util;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.dueris.originspaper.OriginsPaper.LANGUAGE;

public class LangFile {
	public static final Map<String, Map<String, String>> langMap = new ConcurrentHashMap<>();
	private final ResourceLocation key;

	public LangFile(ResourceLocation key, @NotNull JsonObject json) {
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

	public ResourceLocation key() {
		return this.key;
	}
}
