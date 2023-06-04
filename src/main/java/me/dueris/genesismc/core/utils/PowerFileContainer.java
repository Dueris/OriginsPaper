package me.dueris.genesismc.core.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class PowerFileContainer implements Serializable {

    ArrayList<String> keys;
    ArrayList<Object> values;

    public PowerFileContainer(ArrayList<String> keys, ArrayList<Object> values) {
        this.keys = keys;
        this.values = values;
    }

    @Override
    public String toString() {
        return this.keys.toString() + this.values.toString();
    }

    public Object get(String key) {
        int index = this.keys.indexOf(key);
        if (index == -1) return null;
        return this.values.get(index);
    }

    public void add(String key, Object value) {
        this.keys.add(key);
        this.values.add(value);
    }

    public void remove (String key) {
        this.keys.remove(key);
        this.values.remove(this.keys.indexOf(key));
    }

    public boolean contains(String key) {
        for (String k : this.keys) if (k.equals(key)) return true;
        return false;
    }

    public void replace(String key, String newValue) {
        int index = this.keys.indexOf(key);
        if (index == -1) add(key, newValue);
        else this.values.set(index, newValue);
    }

    public ArrayList<String> getKeys() {
        return keys;
    }

    public ArrayList<Object> getValues() {
        return values;
    }
}
