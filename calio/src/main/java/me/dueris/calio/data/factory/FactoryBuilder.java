package me.dueris.calio.data.factory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;

/**
 * This is what is created for the instance, which is provided to the
 * FactoryInstance for creation
 */
public class FactoryBuilder {
    protected File jsonFile;
    protected FactoryElement source;

    public FactoryBuilder(JsonElement source, File jsonFile) {
        this.source = new FactoryElement(source);
        this.jsonFile = jsonFile;
    }

    public FactoryJsonObject getRoot() {
        return this.source.toJsonObject();
    }

    public FactoryElement getAsElement() {
        return this.source;
    }

    public FactoryBuilder cloneFactory() {
        return new FactoryBuilder(this.source.handle, this.jsonFile);
    }

    public File getJsonFile() {
        return this.jsonFile;
    }

    public void putDefault(String key, Object value) {
        JsonObject t = this.source.handle.getAsJsonObject();
        Gson gson = new Gson();
        // Handle JsonElements - Handle properties aswell
        if (value instanceof Number number) {
            t.addProperty(key, number);
        } else if (value instanceof String string) {
            t.addProperty(key, string);
        } else if (value instanceof Boolean bool) {
            t.addProperty(key, bool);
        } else if (value instanceof Character character) {
            t.addProperty(key, character);
        } else if (value instanceof JsonElement element) {
            t.add(key, element);
        } else { // Fallback to make into a string
            t.addProperty(key, gson.toJson(value));
        }
    }
}
