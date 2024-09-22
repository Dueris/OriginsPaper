package io.github.dueris.originspaper.storage;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.event.OriginChangeEvent;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.type.MultiplePower;
import io.github.dueris.originspaper.screen.ScreenNavigator;
import io.github.dueris.originspaper.util.BstatsMetrics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.dueris.originspaper.origin.Origin.EMPTY;

public class OriginComponent {

	private static ServerPlayer getNMS(Player player) {
		return ((CraftPlayer) player).getHandle();
	}

	public static boolean hasOrigin(Player player, ResourceLocation location) {
		Origin origin = Origin.REGISTRY.get(location);
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getAllOrigins().contains(origin);
	}

	public static @NotNull Origin getOrigin(Player player, OriginLayer layer) {
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getOriginOrDefault(EMPTY, layer);
	}

	public static void setOrigin(final @NotNull Entity entity, final OriginLayer layer, final Origin origin) {
		if (!(entity instanceof Player player)) return;
		Map<OriginLayer, Origin> origins = new HashMap<>();
		for (OriginLayer l : PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getOriginLayers()) {
			origins.put(l, PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getOrigin(l));
		}

		if (!origin.getTag().equals(EMPTY.getTag())) {
			PowerHolderComponent.unloadPowers(player, layer.getId(), true);
		}

		for (OriginLayer layers : origins.keySet()) {
			if (layer.getTag().equals(layers.getTag())) {
				origins.replace(layers, origin);
			}
		}

		String originTag = origin.getTag();
		if (!originTag.equals(EMPTY.getTag())) {
			BstatsMetrics.originPopularity(player);
		}

		PlayerPowerRepository repository = PlayerPowerRepository.getOrCreateRepo(getNMS(player));
		preloadPowersFromOrigin(layer, origin, repository);

		repository.setOrigin(origin, layer);

		PowerHolderComponent.loadPowers(player, layer.getId(), true);

		OriginChangeEvent e = new OriginChangeEvent(player, origin, layer, ScreenNavigator.orbChoosing.contains(getNMS(player)));
		Bukkit.getPluginManager().callEvent(e);
		ScreenNavigator.inChoosingLayer.remove(getNMS(player));
	}

	public static void preloadPowersFromOrigin(OriginLayer layer, @NotNull Origin origin, PlayerPowerRepository repository) {
		for (ResourceLocation power : origin.powers()) {
			PowerType rootPower = OriginsPaper.getPower(power);
			if (rootPower == null) {
				PowerHolderComponent.printNotFound(power, origin);
				continue;
			}
			List<PowerType> types = new LinkedList<>(List.of(rootPower));
			if (rootPower instanceof MultiplePower multiplePower) {
				types.addAll(multiplePower.getSubPowers());
			}

			for (PowerType powerType : types) {
				if (powerType == null) {
					PowerHolderComponent.printNotFound(power, origin);
					continue;
				}
				repository.addPower(powerType, layer.getId());
			}
		}
	}

	public static @NotNull Set<OriginLayer> getLayers(Entity entity) {
		if (!(entity instanceof Player player)) return new HashSet<>();
		return PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getOriginLayers();
	}

	public static boolean hasOrigin(Entity entity, OriginLayer originLayer) {
		if (!(entity instanceof Player player)) return false;
		return !PlayerPowerRepository.getOrCreateRepo(getNMS(player)).getOrigin(originLayer).equals(EMPTY);
	}
}
