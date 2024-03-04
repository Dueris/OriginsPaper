package me.dueris.calio.parse;

import me.dueris.calio.builder.NamespaceRemapper;
import me.dueris.calio.builder.inst.FactoryInstance;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class JsonParser {
    public static void parseDirectory(File directory, FactoryInstance factory){
        Arrays.stream(directory.listFiles()).toList().forEach(jsonFile -> {
            try {
                JSONObject remappedJSON = NamespaceRemapper.createRemapped(jsonFile);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
