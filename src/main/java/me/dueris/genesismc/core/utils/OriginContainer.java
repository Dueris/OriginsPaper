package me.dueris.genesismc.core.utils;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class OriginContainer {
    String tag;
    HashMap<String, Object> originLayerFile;
    HashMap<String, Object> originFile;
    ArrayList<PowerContainer> powerContainer;

    public OriginContainer(String tag, HashMap<String, Object> originLayerFile, HashMap<String, Object> originFile, ArrayList<PowerContainer> powerContainer) {
        this.tag = tag;
        this.originLayerFile = originLayerFile;
        this.originFile = originFile;
        this.powerContainer = powerContainer;
    }

    /**
     * @return The customOrigin formatted for debugging, not to be used in other circumstances.
     */
    public String toString() {
        return "Tag: " + this.tag + ", OriginLayerFile: " + this.originLayerFile + ", OriginFile: " + this.originFile + ", PowerContainer {" + this.powerContainer.toString() + "}";
    }


    public String getTag() {
        return new String(this.tag);
    }

    public HashMap<String, Object> getOriginLayerFile() {
        return new  HashMap<String, Object>(this.originLayerFile);
    }

    public HashMap<String, Object> getOriginFile() {
        return new HashMap<String, Object>(this.originFile);
    }

    public ArrayList<PowerContainer> getPowerContainers() {
        return new ArrayList<PowerContainer>(this.powerContainer);
    }


    //origin file
    public String getName() {
        String name = (String) this.originFile.get("name");
        if (name == null) return "No Name";
        return name;
    }

    public String getDescription() {
        String description = (String) this.originFile.get("description");
        if (description == null) return "No Description";
        return description;
    }

    public ArrayList<String> getPowers() {
        ArrayList<String> powers = (ArrayList<String>) this.originFile.get("powers");
        if (powers == null) return new ArrayList<>();
        return powers;
    }

    public String getIcon() {
        Object value = this.originFile.get("icon");
        try {
            if (((JSONObject) value).get("item") != "minecraft:air") return (String) ((JSONObject) value).get("item");
            else return "minecraft:player_head";
        } catch (Exception e) {
            try {
                if (!value.toString().equals("minecraft:air")) return value.toString();
                else return "minecraft:player_head";
            } catch (Exception ex) {
                return "minecraft:player_head";
            }
        }
    }

    public Long getImpact() {
        Long impact = (Long) this.originFile.get("impact");
        if (impact == null) return 1L;
        return impact;
    }

    public Boolean getUnchooseable() {
        Boolean hidden = (Boolean) this.originFile.get("unchoosable");
        if (hidden == null) return false;
        return hidden;
    }

}
