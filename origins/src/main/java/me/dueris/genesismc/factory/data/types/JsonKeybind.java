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
		if (!jsonObject.isPresent("key")) throw new IllegalArgumentException("\"key\" must be present!");
		return new JsonKeybind(jsonObject.getString("key"), jsonObject.getBooleanOrDefault("continuous", false));
	}

	public String getKey() {
		return key;
	}

	public boolean isContinuous() {
		return continuous;
	}
}
