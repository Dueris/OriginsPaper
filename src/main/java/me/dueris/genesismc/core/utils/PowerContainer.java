package me.dueris.genesismc.core.utils;

import java.io.Serializable;

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
        return "powerTag: " + this.powerTag + ", PowerFile: " + this.powerFile.toString() + ", PowerSource: " + this.powerSource.toString();
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

    public String getDesription() {
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

}
