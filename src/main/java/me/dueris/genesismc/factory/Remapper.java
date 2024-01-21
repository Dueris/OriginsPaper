package me.dueris.genesismc.factory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.utils.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Remapper {
	// HashMap<ORIGINAL, REMAPPED>
	// Add things to the remapper here to register new mappings for "to -> from"
	private static HashMap<String, String> mappings = new HashMap();
	static {
		mappings.put("origins", "apoli");
	}

	public static void loadMappings(String originalMapping, String remappedMapping){
		mappings.put(originalMapping, remappedMapping);
	}

	public static void loadMappings(HashMap<String, String> mapping){
		for(String key : mapping.keySet()){
			mappings.put(key, mapping.get(key));
		}
	}
	
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

	private static void remapJsonObject(JSONObject obj){
		for(Object key : obj.keySet()){
			Object valueInst = obj.get(key.toString());
				if(valueInst instanceof String){
					if(key.toString().equalsIgnoreCase("type") && valueInst.toString().startsWith("origins:")){
						obj.put(key, valueInst.toString().replace("origins:", "apoli:"));
					}
				} else if (valueInst instanceof JSONObject){
					remapJsonObject((JSONObject)valueInst);
				} else if (valueInst instanceof JSONArray array){
					for(Object ob : array){
						if (ob instanceof JSONObject){
							remapJsonObject((JSONObject)ob);
						}
					}
				}
		}
	}
	
}