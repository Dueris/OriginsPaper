package me.dueris.calio.data.factory;

import com.google.gson.JsonArray;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FactoryJsonArray {
	public final List<FactoryElement> asList;
	public JsonArray handle;

	public FactoryJsonArray(JsonArray array) {
		this.handle = array;
		this.asList = this.handle.asList().stream().map(FactoryElement::fromJson).collect(Collectors.toList());
	}

	public FactoryElement[] asArray() {
		return this.asList.toArray(new FactoryElement[0]);
	}

	public void setEntries(List<FactoryElement> elements) {
		this.asList.clear();
		this.asList.addAll(elements);
	}

	public List<FactoryElement> asList() {
		return this.asList;
	}

	public List<FactoryJsonObject> asJsonObjectList() {
		return this.asList.stream().map(FactoryElement::toJsonObject).collect(Collectors.toList());
	}

	public List<FactoryNumber> asLongList() {
		return this.asList.stream().map(FactoryElement::getNumber).toList();
	}

	public Iterator<FactoryElement> iterator() {
		return this.asList.iterator();
	}

	public void forEach(Consumer<FactoryElement> consumer) {
		this.asList.forEach(consumer);
	}
}
