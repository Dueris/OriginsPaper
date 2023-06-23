package me.dueris.genesismc.core.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PowerContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
    String powerTag;
    PowerFileContainer powerFile;
    String powerSource;

    /**
     * @param powerTag    The power tag.
     * @param powerFile   The data within a power file.
     * @param powerSource What applied the power to the origin.
     */
    public PowerContainer(String powerTag, PowerFileContainer powerFile, String powerSource) {
        this.powerTag = powerTag;
        this.powerFile = powerFile;
        this.powerSource = powerSource;
    }

    /**
     * @return The power tag.
     */
    public String getTag() {
        return this.powerTag;
    }

    /**
     * @return The power file.
     */
    public PowerFileContainer getFile() {
        return this.powerFile;
    }

    /**
     * @return The source of the power.
     */
    public String getSource() {
        return this.powerSource;
    }


    /**
     * @return The powerContainer formatted for debugging, not to be used in other circumstances.
     */
    @Override
    public String toString() {
        return "powerTag: " + this.powerTag + ", PowerFile: " + this.powerFile.toString() + ", PowerSource: " + this.powerSource;
    }

    /**
     * Changes the name of the power.
     */
    public void setName(String newName) {
        this.powerFile.replace("name", newName);
    }

    /**
     * Changes the description of the power.
     */
    public void setDescription(String newDescription) {
        this.powerFile.replace("description", newDescription);
    }

    /**
     * @return The name of the power. Will return "No Name" if there is no power name present.
     */
    public String getName() {
        Object name = this.powerFile.get("name");
        if (name == null) return "No Name";
        return (String) name;
    }

    /**
     * @return The description of the power. Will return "No Description" if there is no description present.
     */
    public String getDescription() {
        Object description = this.powerFile.get("description");
        if (description == null) return "No Description.";
        return (String) description;
    }

    /**
     * @return Whether the power should be displayed. Will return false if "hidden" is not present.
     */
    public Boolean getHidden() {
        Object hidden = powerFile.get("hidden");
        if (hidden == null) return false;
        return (Boolean) hidden;
    }

    /**
     * @return The type from the power file. Will return "" if there is no type present.
     */
    public String getType() {
        Object type = powerFile.get("type");
        if (type == null) return "";
        return (String) type;
    }

    /**
     * @return Whether the elytra should be displayed. Will return false if "render_elytra" is not present.
     */
    public Boolean getShouldRender() {
        Object render = powerFile.get("render_elytra");
        if (render == null) return false;
        return (Boolean) render;
    }

    /**
     * @return The strength for the night vision power.
     */
    public Long getStrength() {
        Object render = powerFile.get("strength");
        if (render == null) return 1L;
        return (long) render;
    }

    /**
     * @return Modifiers in the power file or null if not found
     */
    public HashMap<String, Object> getModifier() {
        Object obj = powerFile.get("modifier");
        if (obj == null) return new HashMap<>();

        if (obj instanceof JSONObject modifier) {
            HashMap<String, Object> result = new HashMap<>();
            for (Object key : modifier.keySet()) {
                String string_key = (String) key;
                Object value = modifier.get(string_key);
                result.put(string_key, value);
            }
            return result;
        }

        return null;
    }

    /**
     * @return Effects in the power file or null if not found
     */
    public List<String> getEffects() {
        List<String> effects = new ArrayList<>();
        Object obj = powerFile.get("effects");
        if (obj instanceof JSONArray effectsArray) {
            for (Object effectObj : effectsArray) {
                if (effectObj instanceof String effect) {
                    effects.add(effect);
                }
            }
        }
        return effects;
    }


    /**
     * Checks the powerfile for the "condition" tag
     * @return Conditions in the power file or null if not found
     */
    @Deprecated
    public HashMap<String, Object> getCondition() {
        Object obj = powerFile.get("condition");
        if (obj == null) return new HashMap<>();

        if (obj instanceof JSONObject modifier) {
            HashMap<String, Object> result = new HashMap<>();
            for (Object key : modifier.keySet()) {
                String string_key = (String) key;
                Object value = modifier.get(string_key);
                result.put(string_key, value);
            }
            return result;
        }

        return null;
    }

    /**
     * Checks the PowerFile for the specified condition
     * @return A HashMap of the keys and values in the condition or null if the condition type isn't found
     */
    public HashMap<String, Object> getCondition(String conditionType) {
        Object obj = powerFile.get(conditionType);
        if (obj == null) return new HashMap<>();
        if (obj instanceof JSONObject modifier) {
            HashMap<String, Object> result = new HashMap<>();
            for (Object key : modifier.keySet()) {
                String string_key = (String) key;
                Object value = modifier.get(string_key);
                result.put(string_key, value);
            }
            return result;
        }

        return null;
    }

}
