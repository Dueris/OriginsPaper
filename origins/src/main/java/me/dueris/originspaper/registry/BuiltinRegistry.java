package me.dueris.originspaper.registry;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.registry.registries.Layer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BuiltinRegistry {
	private static final Layer[] builtinLayers = new Layer[]{
		new Layer(
			ResourceLocation.parse("apoli:command"),
			0, 0, List.of(), true, false, null, null, false, false, List.of(), false, null, false, true
		)
	};

	public static void bootstrap() {
		for (Layer layer : builtinLayers) {
			OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER).register(layer, ResourceLocation.parse("apoli:command"));
		}
	}
}
