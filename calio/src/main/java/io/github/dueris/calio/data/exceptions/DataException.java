package io.github.dueris.calio.data.exceptions;

public class DataException extends RuntimeException {
	private final Phase phase;
	private final Type type;
	private final String path;
	private final String exceptionMessage;

	protected DataException(String baseMessage, Phase phase, Type type, String path, String exceptionMessage) {
		super(baseMessage);
		this.phase = phase;
		this.type = type;
		this.path = path;
		this.exceptionMessage = exceptionMessage;
	}

	public DataException(Phase phase, String path, String exceptionMessage) {
		this("Error " + String.valueOf(phase) + " data at field", phase, DataException.Type.OBJECT, path, exceptionMessage);
	}

	public DataException(Phase phase, String path, Exception exception) {
		this(phase, path, exception.getMessage());
	}

	public DataException(Phase phase, int index, String exceptionMessage) {
		this("Error " + String.valueOf(phase) + " element at index", phase, DataException.Type.ARRAY, "[" + index + "]", exceptionMessage);
	}

	public DataException(Phase phase, int index, Exception exception) {
		this(phase, index, exception.getMessage());
	}

	public DataException prependArray(int index) {
		String processedPath = "[" + index + "]" + (this.path.isEmpty() ? "" : ".") + this.path;
		return new DataException(super.getMessage(), this.phase, DataException.Type.ARRAY, processedPath, this.exceptionMessage);
	}

	public DataException prepend(String path) {
		String separator = this.type != DataException.Type.ARRAY && !this.path.isEmpty() ? "." : "";
		return new DataException(super.getMessage(), this.phase, DataException.Type.OBJECT, path + separator + this.path, this.exceptionMessage);
	}

	public String getMessage() {
		return this.path.isEmpty() ? this.exceptionMessage : super.getMessage() + " " + this.path + ": " + this.exceptionMessage;
	}

	public static enum Phase {
		READING("decoding"),
		WRITING("encoding"),
		SENDING("sending"),
		RECEIVING("receiving");

		final String name;

		private Phase(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}
	}

	public static enum Type {
		OBJECT,
		ARRAY;

		private Type() {
		}
	}
}
