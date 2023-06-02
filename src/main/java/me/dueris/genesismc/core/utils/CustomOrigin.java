package me.dueris.genesismc.core.utils;

import java.io.File;

public class CustomOrigin {
    String identifier;
    File originLayerFile;
    File originFile;
    PowerContainer powerContainer;

    public CustomOrigin(String identifier, File originLayerFile, File originFile, PowerContainer powerContainer) {
        this.identifier = identifier;
        this.originLayerFile = originLayerFile;
        this.originFile = originFile;
        this.powerContainer = powerContainer;
    }

    public String toString() {
        return "Identifier: " + this.identifier + ", OriginLayerFile: " + this.originLayerFile + ", OriginFile: " + this.originFile + ", PowerContainer {" + this.powerContainer.toString() + "}";
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public File getOriginLayerFile() {
        return this.originLayerFile;
    }

    public File getOriginFile() {
        return this.originFile;
    }

    public PowerContainer getPowerContainer() {
        return this.powerContainer;
    }


}
