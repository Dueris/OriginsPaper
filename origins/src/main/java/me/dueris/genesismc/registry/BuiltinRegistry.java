package me.dueris.genesismc.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.registry.registries.Layer;
import org.bukkit.NamespacedKey;

public class BuiltinRegistry {
	private static final Layer[] builtinLayers;

	static {
		builtinLayers = new Layer[]{
			new Layer(0, new FactoryJsonArray(new JsonArray()), false,
				false, "Command Layer", new FactoryJsonObject(new JsonObject()), "Missing",
				"Missing", false, false, new FactoryJsonArray(new JsonArray()),
				NamespacedKey.fromString("origins:empty"), false, true, 0)
		};
	}

	public static void bootstrap() {
		for (Layer layer : builtinLayers) {
			GenesisMC.getPlugin().registry.retrieve(Registries.LAYER).register(layer);
		}
	}
}
