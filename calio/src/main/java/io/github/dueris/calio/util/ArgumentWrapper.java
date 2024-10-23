package io.github.dueris.calio.util;

public record ArgumentWrapper<T>(T argument, String input) {

	@Deprecated
	public T get() {
		return argument;
	}

	@Deprecated
	public String rawArgument() {
		return input;
	}

}
