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
    FileContainer powerFile;
    String powerSource;

    /**
     * @param powerTag    The power tag.
     * @param powerFile   The data within a power file.
     * @param powerSource What applied the power to the origin.
     */
    public PowerContainer(String powerTag, FileContainer powerFile, String powerSource) {
        this.powerTag = powerTag;
        this.powerFile = powerFile;
        this.powerSource = powerSource;
    }

    public FileContainer getPowerFile(){
        return powerFile;
    }

    /**
     * @return The power tag.
     */
    public String getTag() {
        return this.powerTag;
    }

//    /**
//     * @return The power file.
//     */
//    public FileContainer getFile() {
//        return this.powerFile;
//    }

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

    public ArrayList<Long> getSlots() {
        ArrayList<Long> slots = (ArrayList<Long>) this.powerFile.get("slots");
        if (slots == null) return new ArrayList<>();
        return slots;
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
        if (render == null) return true;
        return (Boolean) render;
    }

    public Boolean getOverlay() {
        Object render = powerFile.get("overlay");
        if (render == null) return false;
        return (Boolean) render;
    }

    /**
     * @return The value "strength" from the power file
     */
    public Long getStrength() {
        Object render = powerFile.get("strength");
        if (render == null) return 1L;
        return (long) render;
    }

    public String getModelRenderType(){
        Object type = powerFile.get("render_type");
        if (type == null) return "original";
        return (String) type;
    }

    /**
     * @return The value "interval" from the power file
     */
    public Long getInterval() {
        Object render = powerFile.get("interval");
        if (render == null) return 10L;
        return (long) render;
    }

    public boolean getDropOnDeath(){
        Object render = powerFile.get("drop_on_death");
        if (render == null) return false;
        return (Boolean) render;
    }

    public Double getColor(String thing){
        Object color = powerFile.get(thing);
        if (color == null) return 0.0;
        return (Double) color;
    }

    /**
     * @return The value "burn_duration" from the power file
     */
    public Long getBurnDuration() {
        Object render = powerFile.get("burn_duration");
        if (render == null) return 100L;
        return (long) render;
    }

    /**
     * @return String value of render_type for power origins:phasing
     */
    public String getRenderType() {
        Object type = powerFile.get("render_type");
        if(type == null) return "blindness";
        return type.toString();
    }

    public String getEffect() {
        Object type = powerFile.get("effect");
        if(type == null) return "blindness";
        return type.toString();
    }

    /**
     * @return LONG view_distance value for power origins:phasing
     */
    public Long getViewDistance() {
        Object distance = powerFile.get("view_distance");
        if(distance == null) return 10L;
        return (Long) distance;
    }

    /**
     * @return The value "tick_rate" from the power file
     */
    public Long getTickRate() {
        Object render = powerFile.get("tick_rate");
        if (render == null) return 10L;
        return (long) render;
    }

    /**
     * @return Should the climbing power be canceled in the rain or not
     */
    public boolean getRainCancel() {
        Object render = powerFile.get("rain_cancel");
        if (render == null) return false;
        return (boolean) render;
    }

    public boolean isInverted() {
        Object render = powerFile.get("inverted");
        if (render == null) return false;
        return (boolean) render;
    }

    public String get(String thing, String defaultValue){
        Object type = powerFile.get(thing);
        if(type == null) {
            return defaultValue;
        }
        return type.toString();
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

    public List<String> getEffects() {
        Object obj = powerFile.get("effects");
        if (obj == null) return new ArrayList<>();

        List<String> effectStrings = new ArrayList<>();

        if (obj instanceof JSONArray array) {
            for (Object value : array) {
                if (value instanceof String) {
                    effectStrings.add((String) value);
                }
            }
        } else if (obj instanceof String) {
            effectStrings.add((String) obj);
        }

        return effectStrings;
    }


    /**
     * @return Head value in the power file or null if not found
     */
    public HashMap<String, Object> getHead() {
        Object obj = powerFile.get("head");
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
     * @return Chest value in the power file or null if not found
     */
    public HashMap<String, Object> getChest() {
        Object obj = powerFile.get("chest");
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
     * @return Legs value in the power file or null if not found
     */
    public HashMap<String, Object> getLegs() {
        Object obj = powerFile.get("legs");
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
     * @return Feet value in the power file or null if not found
     */
    public HashMap<String, Object> getFeet() {
        Object obj = powerFile.get("feet");
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

    public HashMap<String, Object> getKey() {
        Object obj = powerFile.get("key");
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
     * Checks the powerfile for the "condition" tag
     *
     * @return Conditions in the power file or null if not found
     */
    public HashMap<String, Object> getConditionFromString(String getThatThingBecauseIdkWhatImDoingPleaseHelpImTired) {
        Object obj = powerFile.get(getThatThingBecauseIdkWhatImDoingPleaseHelpImTired);
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
     * Checks the powerfile for the "condition" tag
     *
     * @return Conditions in the power file or null if not found
     */
    public HashMap<String, Object> getDamageCondition() {
        Object obj = powerFile.get("damage_condition");
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

    public HashMap<String, Object> getFluidCondition() {
        Object obj = powerFile.get("fluid_condition");
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

    public HashMap<String, Object> getBiomeCondition() {
        Object obj = powerFile.get("fluid_condition");
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

    public HashMap<String, Object> getItemCondition() {
        Object obj = powerFile.get("item_condition");
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

    public HashMap<String, Object> getBlockCondition() {
        Object obj = powerFile.get("block_condition");
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

    public HashMap<String, Object> getThunderModifier() {
        Object obj = powerFile.get("modifier");
        if (obj == null) return new HashMap<>();

        if (obj instanceof JSONObject modifierThing) {
            Object entityConditionObj = modifierThing.get("thunder_modifier");
            if (entityConditionObj instanceof JSONObject thunderThing) {
                HashMap<String, Object> result = new HashMap<>();
                for (Object key : thunderThing.keySet()) {
                    String stringKey = (String) key;
                    Object value = thunderThing.get(stringKey);
                    result.put(stringKey, value);
                }
                return result;
            }
        }

        return new HashMap<>();
    }

    public HashMap<String, Object> getEntityConditionFromDamageCondition() {
        Object obj = powerFile.get("damage_condition");
        if (obj == null) return new HashMap<>();

        if (obj instanceof JSONObject damageCondition) {
            Object entityConditionObj = damageCondition.get("entity_condition");
            if (entityConditionObj instanceof JSONObject entityCondition) {
                HashMap<String, Object> result = new HashMap<>();
                for (Object key : entityCondition.keySet()) {
                    String stringKey = (String) key;
                    Object value = entityCondition.get(stringKey);
                    result.put(stringKey, value);
                }
                return result;
            }
        }

        return new HashMap<>();
    }

    public HashMap<String, Object> getEntityCondition() {
        Object obj = powerFile.get("entity_condition");
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

    public HashMap<String, Object> getPhaseDownCondition() {
        Object obj = powerFile.get("phase_down_condition");
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
     *
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


    public HashMap<String, Object> getBiEntityAction() {
        Object obj = powerFile.get("bientity_action");
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
