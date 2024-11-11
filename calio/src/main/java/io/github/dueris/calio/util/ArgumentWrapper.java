package io.github.dueris.calio.util;

public record ArgumentWrapper<T>(T parsedValue, String input) {

	@Deprecated(forRemoval = true)
	public T get() {
		return parsedValue;
	}

	@Deprecated(forRemoval = true)
	public String rawArgument() {
		return input;
	}

	@Deprecated(forRemoval = true)
	public T argument() {
		return parsedValue();
	}

}
