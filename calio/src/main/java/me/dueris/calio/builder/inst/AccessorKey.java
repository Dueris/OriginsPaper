package me.dueris.calio.builder.inst;

public class AccessorKey {
	private final String directory;
	private final boolean usesTypeDefiner;
	private final int priority;
	private Class<? extends FactoryHolder> ofType;

	public AccessorKey(String directory, int priority, boolean usesTypeDefiner) {
		this.directory = directory;
		this.usesTypeDefiner = usesTypeDefiner;
		this.ofType = null;
		this.priority = priority;
	}

	public AccessorKey(String directory, int priority, boolean usesTypeDefiner, Class<? extends FactoryHolder> ofType) {
		this(directory, priority, usesTypeDefiner);
		this.ofType = ofType;
	}

	public String getDirectory() {
		return directory;
	}

	public boolean isUsesTypeDefiner() {
		return usesTypeDefiner;
	}

	public Class<? extends FactoryHolder> getOfType() {
		return ofType;
	}

	public int getPriority() {
		return priority;
	}
}
