package me.dueris.calio.builder.inst.factory;

import com.google.gson.JsonArray;

import me.dueris.calio.util.IgnoreFactoryValidationCheck;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@IgnoreFactoryValidationCheck
public class FactoryJsonArray {
    public JsonArray handle;

    public FactoryJsonArray(JsonArray array) {
        this.handle = array;
    }

    public FactoryElement[] asArray() {
        return this.handle.asList().stream()
            .map(FactoryElement::fromJson)
            .toArray(FactoryElement[]::new);
    }

    public List<FactoryElement> asList() {
        return this.handle.asList().stream()
            .map(FactoryElement::fromJson)
            .collect(Collectors.toList());
    }

    public List<FactoryJsonObject> asJsonObjectList() {
        return this.asList().stream().map(FactoryElement::toJsonObject)
            .collect(Collectors.toList());
    }

    public List<FactoryNumber> asLongList() {
        return this.asList().stream().map(FactoryElement::getNumber).toList();
    }

    public Iterator<FactoryElement> iterator() {
        return this.asList().iterator();
    }

    public void forEach(Consumer<FactoryElement> consumer) {
        this.asList().forEach(consumer);
    }
}
