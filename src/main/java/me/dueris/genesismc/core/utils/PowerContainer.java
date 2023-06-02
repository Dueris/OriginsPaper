package me.dueris.genesismc.core.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class PowerContainer {

    ArrayList<String> powerIdentifier = new ArrayList<>();
    ArrayList<HashMap<String, Object>> powerFile = new ArrayList<>();
    ArrayList<String> powerSource = new ArrayList<>();

    public PowerContainer() {
    }

    public String toString() {
        return "PowerIdentifier: " + this.powerIdentifier + ", PowerFile: " + this.powerFile.toString() + ", PowerSource: " + this.powerSource.toString();
    }

    public void add(String powerIdentifier, HashMap<String, Object> powerFile, String powerSource) {
        this.powerIdentifier.add(powerIdentifier);
        this.powerFile.add(powerFile);
        this.powerSource.add(powerSource);
    }

    public void removeByIdentifier(String powerIdentifier) {
        int index = this.powerIdentifier.indexOf(powerIdentifier);
        this.powerIdentifier.remove(index);
        this.powerFile.remove(index);
        this.powerSource.remove(index);
    }

    public void removeByFile(HashMap<String, String> powerFile) {
        int index = this.powerFile.indexOf(powerFile);
        this.powerIdentifier.remove(index);
        this.powerFile.remove(index);
        this.powerSource.remove(index);
    }

    public void removeBySource(String powerSource) {
        int index = this.powerSource.indexOf(powerSource);
        this.powerIdentifier.remove(index);
        this.powerFile.remove(index);
        this.powerSource.remove(index);
    }
}
