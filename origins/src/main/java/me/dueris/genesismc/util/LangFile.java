package me.dueris.genesismc.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import me.dueris.calio.registry.Registrable;

public class LangFile implements Registrable{
    private NamespacedKey key;
    private Map<String, String> langMap; // KEY -> OUTPUT // EX: "origin.origins.human.name" -> "Human"

    public LangFile(NamespacedKey key, JsonObject json) {
        this.key = key;
        Map<String, String> foundLang = new HashMap<>();
        for (String jsonKey : json.keySet()) {
            if (json.get(jsonKey).isJsonPrimitive() && json.get(jsonKey).getAsJsonPrimitive().isString()) {
                foundLang.put(jsonKey, json.get(jsonKey).getAsString());
            }
        }
        this.langMap = ImmutableMap.copyOf(foundLang);
        this.langMap.keySet().forEach(System.out::println);
    }

    @Override
    public NamespacedKey key() {
        return this.key;
    }
    
}
