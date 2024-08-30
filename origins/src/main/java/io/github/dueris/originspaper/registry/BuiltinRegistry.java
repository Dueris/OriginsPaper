package io.github.dueris.originspaper.registry;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.types.Impact;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BuiltinRegistry {
	private static final OriginLayer[] builtinLayers = new OriginLayer[]{
		new OriginLayer(
			ResourceLocation.parse("apoli:command"),
			0, 0, List.of(), true, false, null, null, false, false, List.of(), false, null, false, true
		)
	};

	public static void bootstrap() {
		for (OriginLayer layer : builtinLayers) {
			OriginsPaper.getRegistry().retrieve(Registries.LAYER).register(layer, ResourceLocation.parse("apoli:command"));
		}
		Origin.EMPTY = new Origin(
			ResourceLocation.parse("origins:empty"), List.of(), new ItemStack(Items.AIR), true, Integer.MAX_VALUE, Impact.NONE, 0, null, net.minecraft.network.chat.Component.empty(), net.minecraft.network.chat.Component.empty()
		);
		OriginsPaper.getRegistry().retrieve(Registries.ORIGIN).register(Origin.EMPTY, ResourceLocation.parse("origins:empty"));
	}
}
