package me.dueris.calio.data;

import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.calio.data.types.RequiredInstance;

public class FactoryDataDefiner<T> {
	private final String key;
	private final Class<T> type;
	private Object defaultVal;

	public FactoryDataDefiner(String objName, Class<T> type, T defaultVal) {
		this.key = objName;
		this.type = type;
		this.defaultVal = defaultVal;
	}

	public FactoryDataDefiner(String objName, Class<T> type, RequiredInstance defaultVal) {
		this(objName, type, (T) null);
		this.defaultVal = defaultVal;
	}

	public FactoryDataDefiner(String objName, Class<T> type, OptionalInstance defaultVal) {
		this(objName, type, (T) null);
		this.defaultVal = defaultVal;
	}

	public Class<T> getType() {
		return this.type;
	}

	public String getObjName() {
		return this.key;
	}

	public Object getDefaultValue() {
		return this.defaultVal;
	}
}
