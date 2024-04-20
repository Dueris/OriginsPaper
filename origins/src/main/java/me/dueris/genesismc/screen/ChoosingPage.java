package me.dueris.genesismc.screen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;

import me.dueris.calio.registry.Registrable;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import net.minecraft.world.entity.player.Player;

public interface ChoosingPage extends Registrable {
    ItemStack[] createDisplay(Player player, Layer layer);
    ItemStack getChoosingStack(Player player);
    void onChoose(Player player, Layer layer);

    static void registerInstances() {
        ((Registrar<Layer>) GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).values().stream()
            .filter(Layer::isEnabled)
            .forEach((Layer layer) -> {
                ScreenNavigator.layerPages.put(layer, new ArrayList<>());
                List<Origin> choosable = layer.getOriginIdentifiers().stream()
                    .map(CraftApoli::getOrigin)
                    .filter(origin -> !origin.getUnchooseable())
                    .sorted(Comparator.comparingInt(Origin::getOrder))
                    .sorted(Comparator.comparingInt(Origin::getImpact))
                    .toList();

                Origin defaultOrigin = null;
                if (layer.isPresent("default_origin")) {
                    defaultOrigin = choosable.stream()
                        .filter(origin -> origin.getTag().equalsIgnoreCase(layer.getString("default_origin")))
                        .findFirst().orElse(null);
                }

                if (defaultOrigin != null) {
                    choosable.remove(defaultOrigin);
                    choosable.add(0, defaultOrigin);
                }

                ScreenNavigator.layerPages.get(layer).addAll(
                    choosable.stream()
                        .map(OriginPage::new)
                        .collect(Collectors.toList())
                );
            });


        ScreenNavigator.layerPages.values().forEach(list -> {
            for (ChoosingPage page : list) {
                GenesisMC.getPlugin().registry.retrieve(Registries.CHOOSING_PAGE).register(page);
            }
        });
    }
}
