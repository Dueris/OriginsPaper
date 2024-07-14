package me.dueris.originspaper.factory;

import com.google.gson.JsonArray;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.registry.Registrar;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CraftApoli {

	/**
	 * Size of the buffer to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;
	private static final Registrar<Layer> layerRegistrar = OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER);
	private static final Registrar<Origin> originRegistrar = OriginsPaper.getPlugin().registry.retrieve(Registries.ORIGIN);
	private static final Registrar<PowerType> powerRegistrar = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER);
	private static final Collection<Layer> layerValues = new ArrayList<>();
	private static final Collection<Origin> originValues = new ArrayList<>();
	private static final Collection<PowerType> powerValues = new ArrayList<>();
	static Origin empty = new Origin(
		"Empty", "No Origin", 0,
		new ItemStack(Material.BEDROCK), true, new FactoryJsonArray(new JsonArray()),
		new FactoryJsonArray(new JsonArray()), 0, 0
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
		for (Origin o : originRegistrar.values())
			if (o.getTag().equals(originTag)) return o;
		return emptyOrigin();
	}

	public static Layer getLayerFromTag(String layerTag) {
		for (Layer l : layerRegistrar.values())
			if (l.getTag().equals(layerTag)) return l;
		return layerRegistrar.get(ResourceLocation.fromNamespaceAndPath("origins", "origin"));
	}

	public static PowerType getPowerFromTag(String powerTag) {
		for (PowerType p : powerRegistrar.values())
			if (p.getTag().equals(powerTag)) return p;
		return null;
	}

	public static PowerType getPowersFromResourceLocation(ResourceLocation location) {
		return getPowerFromTag(location.toString());
	}

	/**
	 * @return A copy of The null origin.
	 **/
	public static Origin emptyOrigin() {
		return empty;
	}

	public static ArrayList<PowerType> getNestedPowerTypes(PowerType power) {
		ArrayList<PowerType> nested = new ArrayList<>();
		if (power == null) return nested;
		if (power instanceof Multiple multiple) {
			nested.addAll(multiple.getSubPowers());
		}
		return nested;
	}

	public static File datapackDir() {
		return new File(OriginsPaper.server.getWorldPath(LevelResource.DATAPACK_DIR).toAbsolutePath().toString());
	}

	public static File[] datapacksInDir() {
		return datapackDir().listFiles();
	}

	public static void unloadData() {
		OriginsPaper.getPlugin().registry.clearRegistries();
	}

	/**
	 * @return The HashMap serialized into a byte array.
	 **/
	public static String toSaveFormat(HashMap<Layer, Origin> origin, Player p) {
		StringBuilder data = new StringBuilder();
		for (Layer layer : origin.keySet()) {
			if (layer == null) continue;
			Origin layerOrigins = origin.get(layer);
			ArrayList<String> powers = new ArrayList<>();
			if (PowerHolderComponent.playerPowerMapping.get(p).containsKey(layer)) {
				powers.addAll(PowerHolderComponent.playerPowerMapping.get(p).get(layer).stream().map(PowerType::getTag).toList());
			} else {
				powers.addAll(layerOrigins.getPowers());
			}
			int powerSize = powers.size();
			data.append(layer.getTag()).append("|").append(layerOrigins.getTag()).append("|").append(powerSize);
			for (String power : powers) data.append("|").append(power);
			data.append("\n");
		}
//        System.out.println(data.toString());
		return data.toString();
	}

	/**
	 * @return The HashMap serialized into a byte array.
	 **/
	public static String toOriginSetSaveFormat(HashMap<Layer, Origin> origin) {
		StringBuilder data = new StringBuilder();
		for (Layer layer : origin.keySet()) {
			Origin layerOrigins = origin.get(layer);
			List<String> powers = layerOrigins.getPowers();
			int powerSize = 0;
			if (powers != null) powerSize = powers.size();
			data.append(layer.getTag()).append("|").append(layerOrigins.getTag()).append("|").append(powerSize);
			if (powers != null) for (String power : powers) data.append("|").append(power);
			data.append("\n");
		}
//        System.out.println(data.toString());
		return data.toString();
	}

	/**
	 * @return The byte array deserialized into the origin specified by the layer.
	 **/
	public static Origin toOrigin(String originData, Layer originLayer) {
		if (originData != null) {
			try {
				String[] layers = originData.split("\n");
				for (String layer : layers) {
					String[] layerData = layer.split("\\|");
					if (layerRegistrar.get(ResourceLocation.parse(layerData[0])).equals(originLayer)) {
						return CraftApoli.getOrigin(layerData[1]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return CraftApoli.emptyOrigin();
			}
		}
		return CraftApoli.emptyOrigin();
	}

	/**
	 * @return The byte array deserialized into a HashMap of the originLayer and the OriginContainer.
	 **/
	public static HashMap<Layer, Origin> toOrigin(String originData) {
		HashMap<Layer, Origin> containedOrigins = new HashMap<>();
		if (originData == null) {
			layerRegistrar.forEach((key, layer) -> {
				containedOrigins.put(layer, CraftApoli.emptyOrigin());
			});
		} else {
			try {
				String[] layers = originData.split("\n");
				for (String layer : layers) {
					String[] layerData = layer.split("\\|");
					Layer layerContainer = layerRegistrar.get(ResourceLocation.parse(layerData[0]));
					Origin originContainer = CraftApoli.getOrigin(layerData[1]);
					containedOrigins.put(layerContainer, originContainer);
				}
			} catch (Exception e) {
				e.printStackTrace();
				layerRegistrar.forEach((key, layer) -> {
					containedOrigins.put(layer, CraftApoli.emptyOrigin());
				});
				return containedOrigins;
			}
		}
		return containedOrigins;
	}

	/**
	 * @return True if an origin is part of the core origins.
	 **/
	public static Boolean isCoreOrigin(Origin origin) {
		return origin.getTag().equals("origins:arachnid")
			|| origin.getTag().equals("origins:avian")
			|| origin.getTag().equals("origins:blazeborn")
			|| origin.getTag().equals("origins:elytrian")
			|| origin.getTag().equals("origins:enderian")
			|| origin.getTag().equals("origins:feline")
			|| origin.getTag().equals("origins:human")
			|| origin.getTag().equals("origins:merling")
			|| origin.getTag().equals("origins:phantom")
			|| origin.getTag().equals("origins:shulk")
			|| origin.getTag().equals("origins:allay")
			|| origin.getTag().equals("origins:bee")
			|| origin.getTag().equals("origins:creep")
			|| origin.getTag().equals("origins:piglin")
			|| origin.getTag().equals("origins:rabbit")
			|| origin.getTag().equals("origins:sculkling")
			|| origin.getTag().equals("origins:slimeling")
			|| origin.getTag().equals("origins:starborne");
	}

}
