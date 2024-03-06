package me.dueris.genesismc.registry.registries;

import me.dueris.calio.builder.inst.FactoryProvider;
import org.json.simple.JSONArray;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DatapackFile implements Serializable {

	@Serial
	private static final long serialVersionUID = 3L;

	List<String> keys;
	List<Object> values;
	FactoryProvider provider;

	public DatapackFile(List<String> keys, List<Object> values) {
		this.keys = keys;
		this.values = values;
	}

	public DatapackFile(List<String> keys, List<Object> values, FactoryProvider provider) {
		this.keys = keys;
		this.values = values;
		this.provider = provider;
	}

	public FactoryProvider getFactoryProvider() {
		return this.provider;
	}

	@Override
	public String toString() {
		return this.keys.toString() + "\n" + this.values.toString();
	}

	public Object get(String key) {
		int index = this.keys.indexOf(key);
		if (index == -1) return null;
		return this.values.get(index);
	}

	public void addInstance(ArrayList<String> originTags) {
		int index = this.keys.indexOf("origins");
		JSONArray origins = (JSONArray) this.values.get(index);
		origins.addAll(originTags);
		this.values.set(index, origins);
	}

	public void remove(String key) {
		this.keys.remove(key);
		this.values.remove(this.keys.indexOf(key));
	}

	public boolean contains(String key) {
		for (String k : this.keys) if (k.equals(key)) return true;
		return false;
	}

	public void replace(String key, String newValue) {
		int index = this.keys.indexOf(key);
		if (index != -1) this.values.set(index, newValue);
	}

	public List<String> getKeys() {
		return keys;
	}

	public List<Object> getValues() {
		return values;
	}
}
