package io.github.dueris.originspaper.data.types;

public record Keybind(String key, boolean continuous) {
	public static Keybind DEFAULT_KEYBIND = new Keybind("key.origins.primary_active", false);
}
