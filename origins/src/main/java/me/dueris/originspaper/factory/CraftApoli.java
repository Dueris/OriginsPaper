package me.dueris.originspaper.factory;

import io.github.dueris.calio.registry.Registrar;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.data.types.Impact;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.Origin;
import me.dueris.originspaper.registry.registries.OriginLayer;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.LevelResource;
import org.bukkit.entity.Player;
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
	private static final Registrar<OriginLayer> layerRegistrar = OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER);
	private static final Registrar<Origin> originRegistrar = OriginsPaper.getPlugin().registry.retrieve(Registries.ORIGIN);
	private static final Registrar<PowerType> powerRegistrar = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER);
	private static final Collection<OriginLayer> layerValues = new ArrayList<>();
	private static final Collection<Origin> originValues = new ArrayList<>();
	private static final Collection<PowerType> powerValues = new ArrayList<>();
	public static Origin EMPTY_ORIGIN = new Origin(
		ResourceLocation.parse("origins:empty"), List.of(), new ItemStack(Items.AIR), true, Integer.MAX_VALUE, Impact.NONE, 0, null, Component.empty(), Component.empty()
	);

	public static Collection<OriginLayer> getLayersFromRegistry() {
		return layerValues;
	}

	public static Collection<Origin> getOriginsFromRegistry() {
		return originValues;
	}

	public static Collection<PowerType> getPowersFromRegistry() {
		return powerValues;
	}

	public static Origin getOrigin(@NotNull ResourceLocation location) {
		return getOrigin(location.toString());
	}

	public static Origin getOrigin(String originTag) {
		for (Origin o : originRegistrar.values()) {
			if (o.getTag().equals(originTag)) {
				return o;
			}
		}

		return emptyOrigin();
	}

	public static OriginLayer getLayer(ResourceLocation layerTag) {
		return getLayer(layerTag.toString());
	}

	public static OriginLayer getLayer(String layerTag) {
		for (OriginLayer l : layerRegistrar.values()) {
			if (l.getTag().equals(layerTag)) {
				return l;
			}
		}

		return layerRegistrar.get(ResourceLocation.fromNamespaceAndPath("origins", "origin"));
	}

	public static @Nullable PowerType getPower(@NotNull ResourceLocation location) {
		return getPower(location.toString());
	}

	public static @Nullable PowerType getPower(String powerTag) {
		for (PowerType p : powerRegistrar.values()) {
			if (p.getTag().equals(powerTag)) {
				return p;
			}
		}

		return null;
	}

	public static PowerType getPowersFromResourceLocation(@NotNull ResourceLocation location) {
		return getPower(location.toString());
	}

	public static Origin emptyOrigin() {
		return EMPTY_ORIGIN;
	}

	public static @NotNull ArrayList<PowerType> getNestedPowerTypes(PowerType power) {
		ArrayList<PowerType> nested = new ArrayList<>();
		if (power == null) {
			return nested;
		} else {
			// todo
//			if (power instanceof Multiple multiple) {
//				nested.addAll(multiple.getSubPowers());
//			}

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

	public static @NotNull String toSaveFormat(@NotNull HashMap<OriginLayer, Origin> origin, Player p) {
		StringBuilder data = new StringBuilder();

		for (OriginLayer layer : origin.keySet()) {
			if (layer != null) {
				Origin layerOrigins = origin.get(layer);
				ArrayList<String> powers = new ArrayList<>();
				if (PowerHolderComponent.playerPowerMapping.get(p).containsKey(layer)) {
					powers.addAll(PowerHolderComponent.playerPowerMapping.get(p).get(layer).stream().map(PowerType::getTag).toList());
				} else {
					powers.addAll(layerOrigins.powers().stream().map(ResourceLocation::toString).toList());
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

	public static @NotNull String toOriginSetSaveFormat(@NotNull HashMap<OriginLayer, Origin> origin) {
		StringBuilder data = new StringBuilder();

		for (OriginLayer layer : origin.keySet()) {
			Origin layerOrigins = origin.get(layer);
			List<String> powers = layerOrigins.powers().stream().map(ResourceLocation::toString).toList();
			int powerSize = 0;
			powerSize = powers.size();

			data.append(layer.getTag()).append("|").append(layerOrigins.getTag()).append("|").append(powerSize);
			for (String power : powers) {
				data.append("|").append(power);
			}

			data.append("\n");
		}

		return data.toString();
	}

	public static Origin toOrigin(String originData, OriginLayer originLayer) {
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

	public static HashMap<OriginLayer, Origin> toOrigin(String originData) {
		HashMap<OriginLayer, Origin> containedOrigins = new HashMap<>();
		if (originData == null) {
			layerRegistrar.forEach((key, layerx) -> containedOrigins.put(layerx, emptyOrigin()));
		} else {
			try {
				String[] layers = originData.split("\n");

				for (String layer : layers) {
					String[] layerData = layer.split("\\|");
					OriginLayer layerContainer = layerRegistrar.get(ResourceLocation.parse(layerData[0]));
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
