package me.dueris.genesismc.core.utils;

import org.bukkit.Material;
import org.json.simple.JSONObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class OriginContainer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    String tag;
    FileContainer layerFile;
    HashMap<String, Object> originFile;
    ArrayList<PowerContainer> powerContainer;

    /**
     * An object that stores an origin and all the details about it.
     *
     * @param tag             The origin tag.
     * @param layerFile       The origin layer file data.
     * @param originFile      The origin file, parsed into a HashMap.
     * @param powerContainer  An array of powers that the origin has.
     */
    public OriginContainer(String tag, FileContainer layerFile, HashMap<String, Object> originFile, ArrayList<PowerContainer> powerContainer) {
        this.tag = tag;
        this.layerFile = layerFile;
        this.originFile = originFile;
        this.powerContainer = powerContainer;
    }

    /**
     * @return The customOrigin formatted for debugging, not to be used in other circumstances.
     */
    @Override
    public String toString() {
        return "Tag: " + this.tag + ", OriginLayerFile: " + this.layerFile + ", OriginFile: " + this.originFile + ", PowerContainer: " + this.powerContainer.toString();
    }

    /**
     * @return The origin tag.
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * @return The origin layer file parsed into a HashMap.
     */
    public FileContainer getLayerFile() {
        return this.layerFile;
    }

    /**
     * @return The origin file parsed into a HashMap.
     */
    public HashMap<String, Object> getOriginFile() {
        return new HashMap<String, Object>(this.originFile);
    }

    /**
     * @return An array containing all the origin powers.
     */
    public ArrayList<PowerContainer> getPowerContainers() {
        return new ArrayList<>(this.powerContainer);
    }


    //origin file

    /**
     * @return The name of the origin.
     */
    public String getName() {
        String name = (String) this.originFile.get("name");
        if (name == null) return "No Name";
        return name;
    }

    /**
     * @return The description for the origin.
     */
    public String getDescription() {
        String description = (String) this.originFile.get("description");
        if (description == null) return "No Description";
        return description;
    }

    /**
     * @return An array of powers from the origin.
     */
    public ArrayList<String> getPowers() {
        ArrayList<String> powers = (ArrayList<String>) this.originFile.get("powers");
        if (powers == null) return new ArrayList<>();
        return powers;
    }

    /**
     * @return The icon of the origin.
     */
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

    /**
     * @return The icon as a Material Object.
     */
    public Material getMaterialIcon() {
        return Material.valueOf(getIcon().split(":")[1].toUpperCase());
    }

    /**
     * @return The impact of the origin.
     */
    public Long getImpact() {
        Long impact = (Long) this.originFile.get("impact");
        if (impact == null) return 1L;
        return impact;
    }

    /**
     * @return If the origin is choose-able from the choose menu.
     */
    public Boolean getUnchooseable() {
        Boolean hidden = (Boolean) this.originFile.get("unchooseable");
        if (hidden == null) return false;
        return hidden;
    }

    /**
     * @return The PowerContainer with the given type if present in the origin.
     */
    public PowerContainer getPowerFileFromType(String powerType) {
        for (PowerContainer power : getPowerContainers()) {
            if (power.getType().equals(powerType)) return power;
        }
        return null;
    }

    /**
     * @return The name of the layer the origin is in
     */
    public String getLayerName() {
        String name = (String) this.layerFile.get("name");
        if (name == null) return "No layer name found";
        return name;
    }

    public String getLayerTag() {
        String name = (String) this.layerFile.get("name");
        if (name == null) return "No layer name found";
        return name;
    }
}
