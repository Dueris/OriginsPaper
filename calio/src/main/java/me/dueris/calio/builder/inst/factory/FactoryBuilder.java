package me.dueris.calio.builder.inst.factory;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    public File getJsonFile(){
        return this.jsonFile;
    }

    public void putDefault(String key, Object value){
        JsonObject t = this.source.handle.getAsJsonObject();
        Gson gson = new Gson();
        t.addProperty(key, gson.toJson(value));
    }
}
