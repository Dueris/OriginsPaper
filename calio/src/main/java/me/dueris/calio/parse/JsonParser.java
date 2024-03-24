package me.dueris.calio.parse;

import me.dueris.calio.builder.ObjectRemapper;
import me.dueris.calio.builder.inst.AccessorRoot;
import me.dueris.calio.builder.inst.FactoryProvider;
import me.dueris.calio.parse.verification.JsonFactoryValidator;
import me.dueris.calio.registry.impl.CalioRegistry;
import org.bukkit.NamespacedKey;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.Arrays;

public class JsonParser {

	/**
	 * Parses the given directory, remaps JSON files, validates factory, creates instances and puts them into the registry.
	 *
	 * @param  directory   the directory to parse
	 * @param  root        the root accessor
	 * @param  namespace   the namespace for the JSON files
	 * @param  before      the string to prepend to the JSON file names
	 */
	public static void parseDirectory(File directory, AccessorRoot root, String namespace, String before) {
		Arrays.stream(directory.listFiles()).toList().forEach(jsonFile -> {
			try {
				if(!jsonFile.isDirectory()){
					NamespacedKey key = new NamespacedKey(namespace.toLowerCase(), (before + jsonFile.getName().replace(".json", "")).toLowerCase());
					JSONObject remappedJSON = ObjectRemapper.createRemapped(jsonFile, key);
					FactoryProvider validatedFactory = JsonFactoryValidator.validateFactory(new FactoryProvider(remappedJSON), root.getFactoryInst().getValidObjectFactory(), key);
					if (validatedFactory != null) {
						root.getFactoryInst().createInstance(validatedFactory, jsonFile, CalioRegistry.INSTANCE.retrieve(root.getPutRegistry()), key);
					}
				}else{
					parseDirectory(jsonFile, root, namespace, jsonFile.getName() + "/");
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}
