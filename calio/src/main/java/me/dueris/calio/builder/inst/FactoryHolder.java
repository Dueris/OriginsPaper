package me.dueris.calio.builder.inst;

public interface FactoryHolder {
	static FactoryData registerComponents(FactoryData data) {
		return new FactoryData();
	}
}
