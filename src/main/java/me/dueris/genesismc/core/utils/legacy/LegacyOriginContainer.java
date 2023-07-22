package me.dueris.genesismc.core.utils.legacy;

import org.bukkit.Material;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@Deprecated
public class LegacyOriginContainer implements Serializable {
    String tag;
    HashMap<String, Object> originLayerFile;
    HashMap<String, Object> originFile;
    ArrayList<LegacyPowerContainer> powerContainer;

    /**
     * The files are parsed into hashmaps by putting the json key as the key and the value as the value.
     *
     * @param tag             The origin tag.
     * @param originLayerFile The origin layer file, parsed into a HashMap.
     * @param originFile      The origin file, parsed into a HashMap.
     * @param powerContainer  An array of powers that the origin has.
     */
    public LegacyOriginContainer(String tag, HashMap<String, Object> originLayerFile, HashMap<String, Object> originFile, ArrayList<LegacyPowerContainer> powerContainer) {
        this.tag = tag;
        this.originLayerFile = originLayerFile;
        this.originFile = originFile;
        this.powerContainer = powerContainer;
    }

    /**
     * @return The customOrigin formatted for debugging, not to be used in other circumstances.
     */
    @Override
    public String toString() {
        return "Tag: " + this.tag + ", OriginLayerFile: " + this.originLayerFile + ", OriginFile: " + this.originFile + ", PowerContainer: " + this.powerContainer.toString();
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
    public HashMap<String, Object> getOriginLayerFile() {
        return new HashMap<String, Object>(this.originLayerFile);
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
    public ArrayList<LegacyPowerContainer> getPowerContainers() {
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
    public LegacyPowerContainer getPowerFileFromType(String powerType) {
        for (LegacyPowerContainer power : getPowerContainers()) {
            if (power.getType().equals(powerType)) return power;
        }
        return null;
    }
}