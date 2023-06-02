package me.dueris.genesismc.core.utils;

import java.io.File;
import java.util.HashMap;

public class CustomOrigin {
    String identifier;
    HashMap<String, Object> originLayerFile;
    HashMap<String, Object> originFile;
    PowerContainer powerContainer;

    public CustomOrigin(String identifier, HashMap<String, Object> originLayerFile, HashMap<String, Object> originFile, PowerContainer powerContainer) {
        this.identifier = identifier;
        this.originLayerFile = originLayerFile;
        this.originFile = originFile;
        this.powerContainer = powerContainer;
    }

    /**
     * @return The customOrigin formatted for debugging, not to be used in other circumstances.
     */
    public String toString() {
        return "Identifier: " + this.identifier + ", OriginLayerFile: " + this.originLayerFile + ", OriginFile: " + this.originFile + ", PowerContainer {" + this.powerContainer.toString() + "}";
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public HashMap<String, Object> getOriginLayerFile() {
        return this.originLayerFile;
    }

    public HashMap<String, Object> getOriginFile() {
        return this.originFile;
    }

    public PowerContainer getPowerContainer() {
        return this.powerContainer;
    }


}
