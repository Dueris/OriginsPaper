package me.dueris.genesismc.registry;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.registry.registries.Layer;

import java.util.ArrayList;

public class BuiltinRegistry {
	private static final Layer[] builtinLayers;

	static {
		builtinLayers = new Layer[]{new Layer(GenesisMC.apoliIdentifier("command"), new ArrayList<>(), new FactoryJsonObject(new Gson().fromJson("{\"order\":0,\"origins\":[],\"enabled\":false,\"replace\":false,\"name\":\"Command Layer\",\"hidden\":true}", JsonObject.class)))};
	}

	public static void bootstrap() {
		for (Layer layer : builtinLayers) {
			GenesisMC.getPlugin().registry.retrieve(Registries.LAYER).register(layer);
		}
	}
}
