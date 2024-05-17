package me.dueris.calio.data;

import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.calio.util.holders.TriPair;

public class FactoryDataDefiner extends TriPair {
	public <T> FactoryDataDefiner(String objName, Class<T> type, T defaultVal) {
		super(objName, type, defaultVal);
	}

	public <T> FactoryDataDefiner(String objName, Class<T> type, RequiredInstance defaultVal) {
		super(objName, type, defaultVal);
	}

	public <T> FactoryDataDefiner(String objName, Class<T> type, OptionalInstance defaultVal) {
		super(objName, type, defaultVal);
	}

	public Class<?> getType() {
		return (Class<?>) this.second;
	}

	public String getObjName() {
		return (String) this.first;
	}

	public Object getDefaultValue() {
		return this.third;
	}
}
