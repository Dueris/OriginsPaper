package me.dueris.originspaper.screen;

import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.Layer;
import me.dueris.originspaper.registry.registries.Origin;
import net.minecraft.world.entity.player.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface ChoosingPage extends Registrable {
	static void registerInstances() {
		OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER).values().stream()
			.filter(Layer::isEnabled)
			.forEach((Layer layer) -> {
				ScreenNavigator.layerPages.put(layer, new ArrayList<>());
				List<Origin> choosable = layer.getOriginIdentifiers().stream()
					.map(CraftApoli::getOrigin)
					.filter(origin -> !origin.isUnchoosable())
					.sorted(Comparator.comparingInt(Origin::getOrder))
					.sorted(Comparator.comparingInt(Origin::getImpact))
					.collect(Collectors.toCollection(ArrayList::new));

				Origin defaultOrigin = null;
				if (!layer.getDefaultOrigin().asString().equalsIgnoreCase("origins:empty")) {
					defaultOrigin = choosable.stream()
						.filter(origin -> origin.getTag().equalsIgnoreCase(layer.getDefaultOrigin().asString()))
						.findFirst().orElse(null);
				}

				if (defaultOrigin != null) {
					choosable.remove(defaultOrigin);
					choosable.addFirst(defaultOrigin);
				}

				ScreenNavigator.layerPages.get(layer).addAll(
					choosable.stream()
						.map(OriginPage::new)
						.toList()
				);
			});


		ScreenNavigator.layerPages.values().forEach(list -> {
			for (ChoosingPage page : list) {
				OriginsPaper.getPlugin().registry.retrieve(Registries.CHOOSING_PAGE).register(page);
			}
		});
	}

	ItemStack[] createDisplay(Player player, Layer layer);

	ItemStack getChoosingStack(Player player);

	void onChoose(Player player, Layer layer);
}
