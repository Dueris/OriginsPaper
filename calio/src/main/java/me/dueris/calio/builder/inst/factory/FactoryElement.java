package me.dueris.calio.builder.inst.factory;

import com.google.gson.JsonElement;

import me.dueris.calio.util.IgnoreFactoryValidationCheck;

@IgnoreFactoryValidationCheck
public class FactoryElement {
    public JsonElement handle;

    public FactoryElement(JsonElement element) {
        this.handle = element;
    }

    public static FactoryElement fromJson(JsonElement element) {
        return new FactoryElement(element);
    }

    public FactoryJsonObject toJsonObject() {
        return new FactoryJsonObject(this.handle.getAsJsonObject());
    }

    public FactoryJsonArray toJsonArray() {
        return new FactoryJsonArray(this.handle.getAsJsonArray());
    }

    public boolean isJsonObject() {
        return this.handle.isJsonObject();
    }

    public boolean isJsonArray() {
        return this.handle.isJsonArray();
    }

    public boolean isGsonPrimative() {
        return this.handle.isJsonPrimitive();
    }

    public boolean isString() {
        return isGsonPrimative() && this.handle.getAsJsonPrimitive().isString();
    }

    public boolean isBoolean() {
        return isGsonPrimative() && this.handle.getAsJsonPrimitive().isBoolean();
    }

    public boolean isNumber() {
        return isGsonPrimative() && this.handle.getAsJsonPrimitive().isNumber();
    }

    public FactoryNumber getNumber() {
        return new FactoryNumber(this.handle.getAsJsonPrimitive());
    }

    public boolean getBoolean() {
        return this.handle.getAsJsonPrimitive().getAsBoolean();
    }

    public String getString() {
        return this.handle.getAsString();
    }

    public FactoryElement deepCopy() {
        return new FactoryElement(this.handle);
    }
}
