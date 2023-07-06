package me.dueris.genesismc.core.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class FileContainer implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    ArrayList<String> keys;
    ArrayList<Object> values;

    public FileContainer(ArrayList<String> keys, ArrayList<Object> values) {
        this.keys = keys;
        this.values = values;
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

    public void addOrigin(ArrayList<String> originTags) {
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

    public ArrayList<String> getKeys() {
        return keys;
    }

    public ArrayList<Object> getValues() {
        return values;
    }
}
