package me.dueris.genesismc.core.utils;

import org.json.simple.JSONArray;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class LayerContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = 4L;

    String tag;
    FileContainer layerFile;

    public LayerContainer(String tag, FileContainer layerFile) {
        this.tag = tag;
        this.layerFile = layerFile;
    }

    /**
     * @return The LayerContainer formatted for debugging, not to be used in other circumstances.
     */
    @Override
    public String toString() {
        return "Tag = " + tag + " LayerFile = " + layerFile.toString();
    }

    /**
     * @return The tag associated with this layer
     */
    public String getTag() {
        return tag;
    }

    /**
     * @return The file associated with this layer
     */
    public FileContainer getLayerFile() {
        return layerFile;
    }

    /**
     * @return The name of the layer file or tag if null
     */
    public String getName() {
        String name = (String) this.layerFile.get("name");
        if (name == null) return tag;
        return name;
    }

    /**
     * @return The name of the layer file or tag if null
     */
    public boolean getReplace() {
        Boolean replace = (Boolean) this.layerFile.get("replace");
        if (replace == null) return false;
        return replace;
    }

    /**
     * @return An array list of the loaded origins tags
     */
    public ArrayList<String> getOrigins() {
        Object array = layerFile.get("origins");
        if (array instanceof JSONArray origins) return new ArrayList<String>(origins);
        return new ArrayList<>();
    }

    /**
     * @param originTags Adds the specified originTags to the layer. If you only need to pass in one originTag use an array list with one tag.
     */
    public void addOrigin(ArrayList<String> originTags) {
        this.layerFile.addOrigin(originTags);
    }
}
