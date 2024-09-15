package io.github.dueris.originspaper.screen;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public interface ChoosingPage {
	static void registerInstances() {
		ApoliRegistries.ORIGIN_LAYER
			.stream()
			.filter(OriginLayer::isEnabled)
			.forEach(
				layer -> {
					ScreenNavigator.layerPages.put(layer, new LinkedList<>());
					List<Origin> choosable = layer.getOriginIdentifiers()
						.stream()
						.map(OriginsPaper::getOrigin)
						.filter(origin -> !origin.unchoosable())
						.sorted(Comparator.comparingInt(Origin::order))
						.sorted(Comparator.comparingInt(Origin::impactValue))
						.collect(Collectors.toCollection(LinkedList::new));
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
				Registry.register(ApoliRegistries.CHOOSING_PAGE, page.key(), page);
			}
		});
	}

	ItemStack[] createDisplay(Player var1, OriginLayer var2);

	ItemStack getChoosingStack(Player var1);

	void onChoose(Player var1, OriginLayer var2);

	ResourceLocation key();
}
