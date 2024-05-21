package me.dueris.genesismc.screen;

import me.dueris.calio.registry.Registrable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import net.minecraft.world.entity.player.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface ChoosingPage extends Registrable {
	static void registerInstances() {
		GenesisMC.getPlugin().registry.retrieve(Registries.LAYER).values().stream()
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
				GenesisMC.getPlugin().registry.retrieve(Registries.CHOOSING_PAGE).register(page);
			}
		});
	}

	ItemStack[] createDisplay(Player player, Layer layer);

	ItemStack getChoosingStack(Player player);

	void onChoose(Player player, Layer layer);
}
