package me.dueris.genesismc.core.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.Serializable;
import java.util.HashMap;

public class PowerContainer implements Serializable {

    String powerTag;
    PowerFileContainer powerFile;
    String powerSource;


    public PowerContainer(String powerTag, PowerFileContainer powerFile, String powerSource) {
        this.powerTag = powerTag;
        this.powerFile = powerFile;
        this.powerSource = powerSource;
    }

    public String getTag() {
        return this.powerTag;
    }

    public PowerFileContainer getFile() {
        return this.powerFile;
    }

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

    public void setName(String newName) {
        this.powerFile.replace("name", newName);
    }

    public void setDescription(String newDescription) {
        this.powerFile.replace("description", newDescription);
    }

    public String getName() {
        Object name = this.powerFile.get("name");
        if (name == null) return "No Name";
        return (String) name;
    }

    public String getDescription() {
        Object description = this.powerFile.get("description");
        if (description == null) return "No Description.";
        return (String) description;
    }

    public Boolean getHidden() {
        Object hidden = powerFile.get("hidden");
        if (hidden == null) return false;
        return (Boolean) hidden;
    }

    public String getType() {
        Object type = powerFile.get("type");
        if (type == null) return "";
        return (String) type;
    }

    public String getValue(String key) {
        Object type = powerFile.get(key);
        if (type == null) return "";
        return (String) type;
    }

    public Boolean getShouldRender() {
        Object render = powerFile.get("render_elytra");
        if (render == null) return false;
        return (Boolean) render;
    }

    public HashMap<String, Object> getModifier() {
        Object obj = powerFile.get("modifier");
        if (obj == null) return new HashMap<>();
        JSONArray modifier = (JSONArray) obj;
        for (int i = 0;i < modifier.size(); i++) {
            modifier.get(i);
            System.out.println(modifier.get(i));
        }
        return null;
    }
}
