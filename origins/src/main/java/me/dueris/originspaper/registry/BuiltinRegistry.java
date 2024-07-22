package me.dueris.originspaper.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.registry.registries.Layer;
import net.minecraft.resources.ResourceLocation;

public class BuiltinRegistry {
	private static final Layer[] builtinLayers = new Layer[]{
		new Layer(
			0,
			new FactoryJsonArray(new JsonArray()),
			false,
			false,
			"Command Layer",
			new FactoryJsonObject(new JsonObject()),
			"Missing",
			"Missing",
			false,
			false,
			new FactoryJsonArray(new JsonArray()),
			ResourceLocation.parse("origins:empty"),
			false,
			true,
			0
		)
	};

	public static void bootstrap() {
		for (Layer layer : builtinLayers) {
			OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER).register(layer);
		}
	}
}
