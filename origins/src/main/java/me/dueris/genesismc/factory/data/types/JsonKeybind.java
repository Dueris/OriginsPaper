package me.dueris.genesismc.factory.data.types;

import me.dueris.calio.data.factory.FactoryJsonObject;

public class JsonKeybind {
	private final String key;
	private final boolean continuous;

	public JsonKeybind(String key, boolean continuous) {
		this.key = key;
		this.continuous = continuous;
	}

	public static JsonKeybind createJsonKeybind(FactoryJsonObject jsonObject) {
		return new JsonKeybind(jsonObject.isPresent("key") ? jsonObject.getString("key") : "key.origins.primary_active", jsonObject.getBooleanOrDefault("continuous", false));
	}

	public String getKey() {
		return key;
	}

	public boolean isContinuous() {
		return continuous;
	}
}
