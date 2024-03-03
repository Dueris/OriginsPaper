package me.dueris.genesismc.registry.registries;

import me.dueris.calio.registry.Registerable;
import org.bukkit.NamespacedKey;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Power implements Serializable, Registerable {
    @Serial
    private static final long serialVersionUID = 2L;
    NamespacedKey powerTag;
    DatapackFile powerFile;
    boolean originMultiple;
    boolean originMultipleParent;
    String jsonData;
    Power powerParent;

    /**
     * @param powerTag       The power tag.
     * @param powerFile      The data within a power file.
     * @param originMultiple Tells the plugin if its an instance of an origins:multiple sub-power
     */
    public Power(NamespacedKey powerTag, DatapackFile powerFile, String jsonData, boolean originMultiple) {
        this.powerTag = powerTag;
        this.powerFile = powerFile;
        this.originMultiple = originMultiple;
        this.originMultipleParent = false;
        this.jsonData = jsonData;
        this.powerParent = null;
    }

    /**
     * @param powerTag             The power tag.
     * @param powerFile            The data within a power file.
     * @param originMultiple       Tells the plugin if its an instance of an origins:multiple sub-power
     * @param originMultipleParent Tells the plugin if its an origins:multiple parent power
     */
    public Power(NamespacedKey powerTag, DatapackFile powerFile, String jsonData, boolean originMultiple, boolean originMultipleParent) {
        this.powerTag = powerTag;
        this.powerFile = powerFile;
        this.originMultiple = false;
        this.originMultipleParent = originMultipleParent;
        this.powerParent = null;
    }

    /**
     * @param powerTag             The power tag.
     * @param powerFile            The data within a power file.
     * @param originMultiple       Tells the plugin if its an instance of an origins:multiple sub-power
     * @param originMultipleParent Tells the plugin if its an origins:multiple parent power
     * @param powerParent          Tells the plugin what to use as an "Inheritance" for values like the name
     */
    public Power(NamespacedKey powerTag, DatapackFile powerFile, String jsonData, boolean originMultiple, boolean originMultipleParent, Power powerParent) {
        this.powerTag = powerTag;
        this.powerFile = powerFile;
        this.originMultiple = false;
        this.originMultipleParent = originMultipleParent;
        this.jsonData = jsonData;
        this.powerParent = powerParent;
    }

    @Override
    public NamespacedKey getKey(){
        return this.powerTag;
    }

    public DatapackFile getPowerFile() {
        return powerFile;
    }

    public boolean isOriginMultipleSubPower() {
        return this.originMultiple;
    }

    public String getJsonData() {
        return this.jsonData;
    }

    // Used for origins:multiple purposes to try and add an "inheritance" feature for cooldowns
    public Power getPowerParent() {
        return powerParent;
    }

    /**
     * @return The power tag.
     */
    public String getTag() {
        return this.powerTag.asString();
    }

    /**
     * @return If the power is an origins:multiple parent
     */
    public boolean isOriginMultipleParent() {
        return this.originMultipleParent;
    }

    /**
     * @return The powerContainer formatted for debugging, not to be used in other circumstances.
     */
    @Override
    public String toString() {
        return "powerTag: " + this.powerTag + ", PowerFile: " + this.powerFile.toString();
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
     * Changes the name of the power.
     */
    public void setName(String newName) {
        this.powerFile.replace("name", newName);
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
     * Changes the description of the power.
     */
    public void setDescription(String newDescription) {
        this.powerFile.replace("description", newDescription);
    }

    /**
     * @return Whether the power should be displayed. Will return false if "hidden" is not present.
     */
    public Boolean isHidden() {
        Object hidden = powerFile.get("hidden");
        if (hidden == null) return false;
        if (this.isOriginMultipleSubPower()) return true;
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

    public JSONObject get(String key) {
        JSONObject jsonObject = (JSONObject) this.powerFile.get(key);
        if (jsonObject == null) return new JSONObject();
        return jsonObject;
    }

    public boolean getBoolean(String key) {
        Object obj = powerFile.get(key);
        if (obj == null) return false;
        return Boolean.parseBoolean(obj.toString());
    }

    public short getShort(String key) {
        Object obj = powerFile.get(key);
        if (obj == null) return 1;
        return Short.parseShort(obj.toString());
    }

    public long getLong(String key) {
        Object obj = powerFile.get(key);
        if (obj == null) return 1;
        return Long.parseLong(obj.toString());
    }

    public int getInt(String key) {
        Object obj = powerFile.get(key);
        if (obj == null) return 1;
        return Integer.parseInt(obj.toString());
    }

    public float getFloat(String key) {
        Object obj = powerFile.get(key);
        if (obj == null) return 1;
        return Float.parseFloat(obj.toString());
    }

    public double getDouble(String key) {
        Object obj = powerFile.get(key);
        if (obj == null) return 1;
        return Double.parseDouble(obj.toString());
    }

    public Object getObject(String key) {
        Object obj = powerFile.get(key);
        return obj;
    }

    public String getString(String key) {
        Object obj = powerFile.get(key);
        if (obj == null) return null;
        return obj.toString();
    }

    public JSONObject getOrDefault(String key, Object def) {
        JSONObject jsonObject = (JSONObject) this.powerFile.get(key);
        if (jsonObject == null) return (JSONObject) def;
        return jsonObject;
    }

    public boolean getBooleanOrDefault(String key, boolean def) {
        Object obj = powerFile.get(key);
        if (obj == null) return def;
        return Boolean.parseBoolean(obj.toString());
    }

    public short getShortOrDefault(String key, short def) {
        Object obj = powerFile.get(key);
        if (obj == null) return def;
        return Short.parseShort(obj.toString());
    }

    public long getLongOrDefault(String key, long def) {
        Object obj = powerFile.get(key);
        if (obj == null) return def;
        return Long.parseLong(obj.toString());
    }

    public int getIntOrDefault(String key, int def) {
        Object obj = powerFile.get(key);
        if (obj == null) return def;
        return Integer.parseInt(obj.toString());
    }

    public float getFloatOrDefault(String key, float def) {
        Object obj = powerFile.get(key);
        if (obj == null) return def;
        return Float.parseFloat(obj.toString());
    }

    public double getDoubleOrDefault(String key, double def) {
        Object obj = powerFile.get(key);
        if (obj == null) return def;
        return Double.parseDouble(obj.toString());
    }

    public Object getObjectOrDefault(String key, Object def) {
        Object obj = powerFile.get(key);
        if (obj == null) return def;
        return obj;
    }

    public String getStringOrDefault(String key, String def) {
        Object obj = powerFile.get(key);
        if (obj == null) return def;
        return obj.toString();
    }

    /**
     * @return Modifiers in the power file or null if not found
     */
    public List<HashMap<String, Object>> getPossibleModifiers(String singular, String plural) {
        Object obj = powerFile.get(singular);
        if (obj == null) {
            obj = powerFile.get(plural);
        }

        List<HashMap<String, Object>> result = new ArrayList<>();

        if (obj instanceof JSONArray jsonArray) {
            for (Object item : jsonArray) {
                if (item instanceof JSONObject jsonObject) {
                    HashMap<String, Object> itemMap = new HashMap<>();
                    for (Object innerKey : jsonObject.keySet()) {
                        String string_key = (String) innerKey;
                        Object value = jsonObject.get(string_key);
                        itemMap.put(string_key, value);
                    }
                    result.add(itemMap);
                }
            }
        } else if (obj instanceof JSONObject jsonObject) {
            HashMap<String, Object> itemMap = new HashMap<>();
            for (Object innerKey : jsonObject.keySet()) {
                String string_key = (String) innerKey;
                Object value = jsonObject.get(string_key);
                itemMap.put(string_key, value);
            }
            result.add(itemMap);
        }
        return result;
    }

    public JSONArray getJsonArray(String thing) {
        Object obj = powerFile.get(thing);
        if (obj == null) return new JSONArray();

        if (obj instanceof JSONArray array) {
            return array;
        }

        return new JSONArray();
    }

    public List<String> getStringList(String key) {
        Object obj = powerFile.get(key);
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

    public List<Integer> getIntList(String key) {
        Object obj = powerFile.get(key);
        if (obj == null) return new ArrayList<>();

        List<Integer> effectStrings = new ArrayList<>();

        if (obj instanceof JSONArray array) {
            for (Object value : array) {
                if (value instanceof Integer) {
                    effectStrings.add((Integer) value);
                }
            }
        } else if (obj instanceof Integer) {
            effectStrings.add((Integer) obj);
        }

        return effectStrings;
    }

    public List<Long> getLongList(String key) {
        Object obj = powerFile.get(key);
        if (obj == null) return new ArrayList<>();

        List<Long> effectStrings = new ArrayList<>();

        if (obj instanceof JSONArray array) {
            for (Object value : array) {
                if (value instanceof Long) {
                    effectStrings.add((Long) value);
                }
            }
        } else if (obj instanceof Long) {
            effectStrings.add((Long) obj);
        }

        return effectStrings;
    }

    public List<Double> getDoubleList(String key) {
        Object obj = powerFile.get(key);
        if (obj == null) return new ArrayList<>();

        List<Double> effectStrings = new ArrayList<>();

        if (obj instanceof JSONArray array) {
            for (Object value : array) {
                if (value instanceof Double) {
                    effectStrings.add((Double) value);
                }
            }
        } else if (obj instanceof Double) {
            effectStrings.add((Double) obj);
        }

        return effectStrings;
    }

    public List<Float> getFloatList(String key) {
        Object obj = powerFile.get(key);
        if (obj == null) return new ArrayList<>();

        List<Float> effectStrings = new ArrayList<>();

        if (obj instanceof JSONArray array) {
            for (Object value : array) {
                if (value instanceof String) {
                    effectStrings.add((Float) value);
                }
            }
        } else if (obj instanceof Float) {
            effectStrings.add((Float) obj);
        }

        return effectStrings;
    }

    /**
     * Checks the powerfile for the "condition" tag
     *
     * @return Conditions in the power file or null if not found
     */
    public List<JSONObject> getJsonListSingularPlural(String singular, String plural) {
        Object obj = powerFile.get(singular);
        if (obj == null) {
            obj = powerFile.get(plural);
        }

        List<JSONObject> result = new ArrayList<>();

        if (obj instanceof JSONArray jsonArray) {
            for (Object item : jsonArray) {
                if (item instanceof JSONObject jsonObject) {
                    result.add(jsonObject);
                }
            }
        } else if (obj instanceof JSONObject jsonObject) {
            result.add(jsonObject);
        }
        return result;
    }

    public JSONObject getBiEntityAction() {
        Object obj = powerFile.get("bientity_action");
        if (obj instanceof JSONObject modifier) {
            return modifier;
        }
        return new JSONObject();
    }

    public JSONObject getItemAction() {
        Object obj = powerFile.get("item_action");
        if (obj instanceof JSONObject modifier) {
            return modifier;
        }
        return new JSONObject();
    }

    public JSONObject getAction(String string) {
        Object obj = powerFile.get(string);
        if (obj instanceof JSONObject modifier) {
            return modifier;
        }
        return new JSONObject();
    }

    public JSONObject getActionOrNull(String string) {
        Object obj = powerFile.get(string);
        if (obj instanceof JSONObject modifier) {
            return modifier;
        }
        return null;
    }

    public JSONObject getEntityAction() {
        Object obj = powerFile.get("entity_action");
        if (obj instanceof JSONObject modifier) {
            return modifier;
        }
        return new JSONObject();
    }

    public JSONObject getBlockAction() {
        Object obj = powerFile.get("block_action");
        if (obj instanceof JSONObject modifier) {
            return modifier;
        }
        return new JSONObject();
    }

}
