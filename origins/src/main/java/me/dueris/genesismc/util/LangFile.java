package me.dueris.genesismc.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import me.dueris.calio.registry.Registrable;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

public class LangFile implements Registrable {
	private final NamespacedKey key;
	private final Map<String, String> langMap; // KEY -> OUTPUT // EX: "origin.origins.human.name" -> "Human"

	public LangFile(NamespacedKey key, JsonObject json) {
		this.key = key;
		Map<String, String> foundLang = new HashMap<>();
		for (String jsonKey : json.keySet()) {
			if (json.get(jsonKey).isJsonPrimitive() && json.get(jsonKey).getAsJsonPrimitive().isString()) {
				foundLang.put(jsonKey, json.get(jsonKey).getAsString());
			}
		}
		this.langMap = ImmutableMap.copyOf(foundLang);
	}

	@Override
	public NamespacedKey key() {
		return this.key;
	}

}
