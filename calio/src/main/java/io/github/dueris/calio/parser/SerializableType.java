package io.github.dueris.calio.parser;

public enum SerializableType {
	DEFAULT, REQUIRED, NULLABLE;

	static SerializableType build(boolean a, boolean b) {
		if (!a && !b) return NULLABLE;
		if (!a) return DEFAULT;
		if (!b) return REQUIRED;
		return NULLABLE;
	}
}
