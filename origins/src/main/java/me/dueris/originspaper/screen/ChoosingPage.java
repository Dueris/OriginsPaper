package me.dueris.originspaper.screen;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.Layer;
import me.dueris.originspaper.registry.registries.Origin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface ChoosingPage {
	static void registerInstances() {
		OriginsPaper.getPlugin()
			.registry
			.retrieve(Registries.LAYER)
			.values()
			.stream()
			.filter(Layer::isEnabled)
			.forEach(
				layer -> {
					ScreenNavigator.layerPages.put(layer, new ArrayList<>());
					List<Origin> choosable = layer.getOriginIdentifiers()
						.stream()
						.map(CraftApoli::getOrigin)
						.filter(origin -> !origin.unchoosable())
						.sorted(Comparator.comparingInt(Origin::order))
						.sorted(Comparator.comparingInt(Origin::impactValue))
						.collect(Collectors.toCollection(ArrayList::new));
					Origin defaultOrigin = null;
					if (layer.getDefaultOrigin() != null && !layer.getDefaultOrigin().toString().equalsIgnoreCase("origins:empty")) {
						defaultOrigin = choosable.stream()
							.filter(origin -> origin.getTag().equalsIgnoreCase(layer.getDefaultOrigin().toString()))
							.findFirst()
							.orElse(null);
					}

					if (defaultOrigin != null) {
						choosable.remove(defaultOrigin);
						choosable.addFirst(defaultOrigin);
					}

					ScreenNavigator.layerPages.get(layer).addAll(choosable.stream().map(OriginPage::new).toList());
				}
			);
		ScreenNavigator.layerPages.values().forEach(list -> {
			for (ChoosingPage page : list) {
				OriginsPaper.getPlugin().registry.retrieve(Registries.CHOOSING_PAGE).register(page, page.key());
			}
		});
	}

	ItemStack[] createDisplay(Player var1, Layer var2);

	ItemStack getChoosingStack(Player var1);

	void onChoose(Player var1, Layer var2);

	ResourceLocation key();
}
