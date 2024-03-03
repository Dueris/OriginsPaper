package me.dueris.genesismc.factory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import me.dueris.genesismc.GenesisMC;

public class JsonValidator {
    /**
     * Validates if the JSONFile is actually able to be parsed correctly, and informs the logger if its not
     * @param filePath
     */
    public static void validateJsonFile(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            getLogger().warning("File not found: " + filePath);
            return;
        }

        if (!filePath.toLowerCase().endsWith(".json")) {
            getLogger().warning("Not a JSON file: " + filePath);
            return;
        }

        try {
            JsonObject rootObject = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();

            if (!file.getName().equals(file.getName().toLowerCase())) {
                getLogger().warning("File name should be all lowercase: " + file.getName());
            }

            if (!rootObject.has("type")) {
                getLogger().warning("Root object is missing 'type' parameter.");
            }

            if (rootObject.has("type") && rootObject.get("type").getAsString().equals("apoli:multiple")) {
                for (String key : rootObject.keySet()) {
                    if (!key.equals("type")) {
                        if (!key.equals(key.toLowerCase())) {
                            getLogger().warning("JSONObject name should be lowercase: " + key);
                        }

                        JsonObject jsonObject = rootObject.getAsJsonObject(key);
                        if (!jsonObject.has("type")) {
                            getLogger().warning("JSONObject is missing 'type' parameter: " + key);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            getLogger().warning("Error reading JSON file: " + e.getMessage());
        } catch (JsonParseException e) {
            getLogger().warning("Error parsing JSON: " + e.getMessage());
        }
    }

    private static Logger getLogger() {
        return GenesisMC.getPlugin().getLogger();
    }
}
