package me.dueris.genesismc.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.DatapackRepository;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class JsonGetter {
	public static String getJsonString(NamespacedKey key, String subFolder) {
		AtomicReference<String> returnValue = new AtomicReference<>();
		for (DatapackRepository repository : ((Registrar<DatapackRepository>) GenesisMC.getPlugin().registry.retrieve(Registries.PACK_SOURCE)).values()) {
			for (File pack : repository.getPath().toFile().listFiles()) {
				File dataFolder = new File(pack, "data");
				if (dataFolder.exists()) {
					File namespaceFolder = new File(dataFolder, key.getNamespace());
					if (namespaceFolder.exists()) {
						File subFolderFile = new File(namespaceFolder, subFolder);
						if (subFolderFile.exists()) {
							Arrays.stream(subFolderFile.listFiles()).forEach(jsonFile -> {
								if (jsonFile.getName().startsWith(key.getKey())) {
									Gson gson = new Gson();
									try {
										returnValue.set(gson.fromJson(new JsonReader(new FileReader(jsonFile)), JsonObject.class).toString());
									} catch (FileNotFoundException e) {
										throw new RuntimeException(e);
									}
								}
							});
						}
					}
				}
			}
		}
		return returnValue.get();
	}
}
