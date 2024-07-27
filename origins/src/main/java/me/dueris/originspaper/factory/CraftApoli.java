package me.dueris.originspaper.factory;

import com.google.gson.JsonArray;
import io.github.dueris.calio.registry.Registrar;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.powers.apoli.Multiple;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.Layer;
import me.dueris.originspaper.registry.registries.Origin;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelResource;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CraftApoli {
	private static final int BUFFER_SIZE = 4096;
	private static final Registrar<Layer> layerRegistrar = OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER);
	private static final Registrar<Origin> originRegistrar = OriginsPaper.getPlugin().registry.retrieve(Registries.ORIGIN);
	private static final Registrar<PowerType> powerRegistrar = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER);
	private static final Collection<Layer> layerValues = new ArrayList<>();
	private static final Collection<Origin> originValues = new ArrayList<>();
	private static final Collection<PowerType> powerValues = new ArrayList<>();
	static Origin empty = new Origin(
		"Empty", "No Origin", 0, new ItemStack(Material.BEDROCK), true, new FactoryJsonArray(new JsonArray()), new FactoryJsonArray(new JsonArray()), 0, 0
	).ofResourceLocation(ResourceLocation.parse("origins:empty"));

	public static Collection<Layer> getLayersFromRegistry() {
		return layerValues;
	}

	public static Collection<Origin> getOriginsFromRegistry() {
		return originValues;
	}

	public static Collection<PowerType> getPowersFromRegistry() {
		return powerValues;
	}

	public static Origin getOrigin(String originTag) {
		for (Origin o : originRegistrar.values()) {
			if (o.getTag().equals(originTag)) {
				return o;
			}
		}

		return emptyOrigin();
	}

	public static Layer getLayerFromTag(String layerTag) {
		for (Layer l : layerRegistrar.values()) {
			if (l.getTag().equals(layerTag)) {
				return l;
			}
		}

		return layerRegistrar.get(ResourceLocation.fromNamespaceAndPath("origins", "origin"));
	}

	public static @Nullable PowerType getPowerFromTag(String powerTag) {
		for (PowerType p : powerRegistrar.values()) {
			if (p.getTag().equals(powerTag)) {
				return p;
			}
		}

		return null;
	}

	public static PowerType getPowersFromResourceLocation(@NotNull ResourceLocation location) {
		return getPowerFromTag(location.toString());
	}

	public static Origin emptyOrigin() {
		return empty;
	}

	public static @NotNull ArrayList<PowerType> getNestedPowerTypes(PowerType power) {
		ArrayList<PowerType> nested = new ArrayList<>();
		if (power == null) {
			return nested;
		} else {
			if (power instanceof Multiple multiple) {
				nested.addAll(multiple.getSubPowers());
			}

			return nested;
		}
	}

	@Contract(" -> new")
	public static @NotNull File datapackDir() {
		return new File(OriginsPaper.server.getWorldPath(LevelResource.DATAPACK_DIR).toAbsolutePath().toString());
	}

	public static File[] datapacksInDir() {
		return datapackDir().listFiles();
	}

	public static void unloadData() {
		OriginsPaper.getPlugin().registry.clearRegistries();
	}

	public static @NotNull String toSaveFormat(@NotNull HashMap<Layer, Origin> origin, Player p) {
		StringBuilder data = new StringBuilder();

		for (Layer layer : origin.keySet()) {
			if (layer != null) {
				Origin layerOrigins = origin.get(layer);
				ArrayList<String> powers = new ArrayList<>();
				if (PowerHolderComponent.playerPowerMapping.get(p).containsKey(layer)) {
					powers.addAll(PowerHolderComponent.playerPowerMapping.get(p).get(layer).stream().map(PowerType::getTag).toList());
				} else {
					powers.addAll(layerOrigins.getPowers());
				}

				int powerSize = powers.size();
				data.append(layer.getTag()).append("|").append(layerOrigins.getTag()).append("|").append(powerSize);

				for (String power : powers) {
					data.append("|").append(power);
				}

				data.append("\n");
			}
		}

		return data.toString();
	}

	public static @NotNull String toOriginSetSaveFormat(@NotNull HashMap<Layer, Origin> origin) {
		StringBuilder data = new StringBuilder();

		for (Layer layer : origin.keySet()) {
			Origin layerOrigins = origin.get(layer);
			List<String> powers = layerOrigins.getPowers();
			int powerSize = 0;
			if (powers != null) {
				powerSize = powers.size();
			}

			data.append(layer.getTag()).append("|").append(layerOrigins.getTag()).append("|").append(powerSize);
			if (powers != null) {
				for (String power : powers) {
					data.append("|").append(power);
				}
			}

			data.append("\n");
		}

		return data.toString();
	}

	public static Origin toOrigin(String originData, Layer originLayer) {
		if (originData != null) {
			try {
				String[] layers = originData.split("\n");

				for (String layer : layers) {
					String[] layerData = layer.split("\\|");
					if (layerRegistrar.get(ResourceLocation.parse(layerData[0])).equals(originLayer)) {
						return getOrigin(layerData[1]);
					}
				}
			} catch (Exception var8) {
				var8.printStackTrace();
				return emptyOrigin();
			}
		}

		return emptyOrigin();
	}

	public static HashMap<Layer, Origin> toOrigin(String originData) {
		HashMap<Layer, Origin> containedOrigins = new HashMap<>();
		if (originData == null) {
			layerRegistrar.forEach((key, layerx) -> containedOrigins.put(layerx, emptyOrigin()));
		} else {
			try {
				String[] layers = originData.split("\n");

				for (String layer : layers) {
					String[] layerData = layer.split("\\|");
					Layer layerContainer = layerRegistrar.get(ResourceLocation.parse(layerData[0]));
					Origin originContainer = getOrigin(layerData[1]);
					containedOrigins.put(layerContainer, originContainer);
				}
			} catch (Exception var10) {
				var10.printStackTrace();
				layerRegistrar.forEach((key, layerx) -> containedOrigins.put(layerx, emptyOrigin()));
				return containedOrigins;
			}
		}

		return containedOrigins;
	}

}
