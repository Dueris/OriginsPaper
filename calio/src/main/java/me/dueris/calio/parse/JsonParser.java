package me.dueris.calio.parse;

import me.dueris.calio.builder.NamespaceRemapper;
import me.dueris.calio.builder.inst.AccessorRoot;
import me.dueris.calio.builder.inst.FactoryProvider;
import me.dueris.calio.parse.verification.JsonFactoryValidator;
import me.dueris.calio.registry.impl.CalioRegistry;
import org.bukkit.NamespacedKey;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.Arrays;

public class JsonParser {
	public static void parseDirectory(File directory, AccessorRoot root, String namespace, String before) {
		Arrays.stream(directory.listFiles()).toList().forEach(jsonFile -> {
			try {
				if(!jsonFile.isDirectory()){
					JSONObject remappedJSON = NamespaceRemapper.createRemapped(jsonFile);
					NamespacedKey key = new NamespacedKey(namespace, before + jsonFile.getName().replace(".json", ""));
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
