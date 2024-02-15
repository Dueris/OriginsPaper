package me.dueris.genesismc.factory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class PowerRemapper {
    public static JSONObject createRemapped(File file) {
        try {
            JSONObject powerParser = (JSONObject) new JSONParser().parse(new FileReader(file));
            remapJsonObject(powerParser);
            return powerParser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private static void remapJsonObject(JSONObject obj) {
        for (Object key : obj.keySet()) {
            Object valueInst = obj.get(key.toString());
            if (valueInst instanceof String) {
                if (key.toString().equalsIgnoreCase("type") && valueInst.toString().startsWith("origins:")) {
                    obj.put(key, valueInst.toString().replace("origins:", "apoli:"));
                }
            } else if (valueInst instanceof JSONObject) {
                remapJsonObject((JSONObject) valueInst);
            } else if (valueInst instanceof JSONArray array) {
                for (Object ob : array) {
                    if (ob instanceof JSONObject) {
                        remapJsonObject((JSONObject) ob);
                    }
                }
            }
        }
    }

}