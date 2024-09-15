package io.github.dueris.originspaper.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import io.github.dueris.originspaper.storage.OriginConfiguration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
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
		Registry.register(ApoliRegistries.LANG, location, new LangFile(new Gson().fromJson(langInput, JsonObject.class)));
	}

	public static @NotNull Component translatable(String original, Object... args) {
		if (langMap.get(LANGUAGE).containsKey(original)) {
			return Component.literal(fixArgs(langMap.get(LANGUAGE).get(original), args));
		}

		return Component.translatable(original);
	}

	private static @NotNull String fixArgs(@NotNull String template, Object... values) {
		if (!template.contains("%s")) return template;
		StringBuilder result = new StringBuilder();
		int lastIndex = 0;
		int placeholderIndex = 0;

		for (int i = 0; i < template.length(); i++) {
			if (i + 2 < template.length() && template.charAt(i) == '%' && template.charAt(i + 1) == 's') {
				if (placeholderIndex < values.length) {
					result.append(template, lastIndex, i);
					result.append(format(values[placeholderIndex]));
					lastIndex = i + 2;
					placeholderIndex++;
				}
				i++;
			}
		}
		result.append(template.substring(lastIndex));

		return result.toString();
	}

	/**
	 * @return The object as a string. If its a component then serialize it to a String text
	 */
	private static String format(Object object) {
		if (object instanceof Component component) {
			return component.getString();
		} else if (object instanceof net.kyori.adventure.text.Component component) {
			return translatable(PlainTextComponentSerializer.plainText().serialize(component)).getString();
		}

		return object.toString();
	}

}
