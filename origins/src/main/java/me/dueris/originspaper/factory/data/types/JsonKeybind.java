package me.dueris.originspaper.factory.data.types;

import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;
import org.jetbrains.annotations.NotNull;

public record JsonKeybind(String key, boolean continuous) {

	public static @NotNull JsonKeybind createJsonKeybind(@NotNull FactoryElement element) {
		if (element.isJsonObject()) {
			FactoryJsonObject jsonObject = element.toJsonObject();
			return new JsonKeybind(
				jsonObject.isPresent("key") ? jsonObject.getString("key") : "key.origins.primary_active", jsonObject.getBooleanOrDefault("continuous", false)
			);
		} else {
			String r = element.getString();
			return switch (r) {
				case "primary" -> new JsonKeybind("key.origins.primary_active", false);
				case "secondary" -> new JsonKeybind("key.origins.secondary_active", false);
				default -> new JsonKeybind(r, false);
			};
		}
	}
}
