package io.github.dueris.originspaper.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.storage.OriginConfiguration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.dueris.originspaper.OriginsPaper.LANGUAGE;

public class LangFile {
	public static final Map<String, Map<String, String>> langMap = new ConcurrentHashMap<>();

	public LangFile(@NotNull JsonObject json) {
		Map<String, String> foundLang = new HashMap<>();

		for (String jsonKey : json.keySet()) {
			if (json.get(jsonKey).isJsonPrimitive() && json.get(jsonKey).getAsJsonPrimitive().isString()) {
				foundLang.put(jsonKey, json.get(jsonKey).getAsString());
			}
		}

		langMap.put(OriginConfiguration.getConfiguration().getString("language"), foundLang);
	}

	public static void init() {
		LANGUAGE = OriginConfiguration.getConfiguration().getString("language", "en_us");
		String langInput = Util.readResource("/assets/origins/lang/%%.json".replace("%%", LANGUAGE));
		ResourceLocation location = ResourceLocation.parse("origins:" + LANGUAGE);
		OriginsPaper.getPlugin().registry.retrieve(Registries.LANG).register(new LangFile(new Gson().fromJson(langInput, JsonObject.class)), location);
	}

	public static String transform(String original) {
		if (langMap.get(LANGUAGE).containsKey(original)) {
			return langMap.get(LANGUAGE).get(original);
		}

		return original;
	}

}