package me.dueris.genesismc.core.utils;

import org.json.simple.JSONObject;

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
        return "Tag = "+tag+" LayerFile = "+layerFile.toString();
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
        Object obj = layerFile.get("origins");
        if (obj == null) return new ArrayList<>();

        if (obj instanceof JSONObject modifier) {
            ArrayList<String> result = new ArrayList<>();
            for (Object key : modifier.keySet()) {
                String string_key = (String) key;
                String value = (String) modifier.get(string_key);
                result.add(value);
            }
            return result;
        }

        return null;
    }
}
