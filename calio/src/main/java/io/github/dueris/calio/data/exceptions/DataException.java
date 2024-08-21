package io.github.dueris.calio.data.exceptions;

import java.util.Locale;

public class DataException extends RuntimeException {
	private final Phase phase;
	private final Exception exception;
	private String path;

	public DataException(Phase phase, String path, Exception exception) {
		super("Error " + phase.name().toLowerCase(Locale.ROOT) + " data field");
		this.phase = phase;
		this.path = path;
		this.exception = exception;
	}

	public DataException prepend(String path) {
		this.path = path + "." + this.path;
		return this;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + " at " + path + ": " + exception.getMessage();
	}

	public enum Phase {
		READING,
		RECEIVING,
		WRITING
	}
}
