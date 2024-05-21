package me.dueris.genesismc.factory.data.types;

import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;

public record JsonKeybind(String key, boolean continuous) {

	public static JsonKeybind createJsonKeybind(FactoryElement element) {
		if (element.isJsonObject()) {
			FactoryJsonObject jsonObject = element.toJsonObject();
			return new JsonKeybind(jsonObject.isPresent("key") ? jsonObject.getString("key") : "key.origins.primary_active", jsonObject.getBooleanOrDefault("continuous", false));
		} else {
			return new JsonKeybind(element.getString(), false);
		}
	}

}
