package me.dueris.genesismc.core.utils;

import java.io.File;
import java.util.ArrayList;

public class PowerContainer {

    ArrayList<String> powerIdentifier = new ArrayList<>();
    ArrayList<File> powerFile = new ArrayList<>();
    ArrayList<String> powerSource = new ArrayList<>();

    public PowerContainer() {
    }

    public String toString() {
        return "PowerIdentifier: " + this.powerIdentifier + ", PowerFile: " + this.powerFile.toString() + ", PowerSource: " + this.powerSource.toString();
    }

    public void add(String powerIdentifier, File powerFile, String powerSource) {
        this.powerIdentifier.add(powerIdentifier);
        this.powerFile.add(powerFile);
        this.powerSource.add(powerSource);
    }

    public void removeBySource(String powerSource) {
        int index = this.powerSource.indexOf(powerSource);
        this.powerIdentifier.remove(index);
        this.powerFile.remove(index);
        this.powerSource.remove(index);
    }

    public void removeByIdentifier(String powerIdentifier) {
        int index = this.powerIdentifier.indexOf(powerIdentifier);
        this.powerIdentifier.remove(index);
        this.powerFile.remove(index);
        this.powerSource.remove(index);
    }

    public void removeByFile(File powerFile) {
        int index = this.powerFile.indexOf(powerFile);
        this.powerIdentifier.remove(index);
        this.powerFile.remove(index);
        this.powerSource.remove(index);
    }
}
