package me.dueris.genesismc.registry.registries;

import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.inst.FactoryInstance;
import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.calio.builder.inst.FactoryProvider;
import me.dueris.calio.registry.Registerable;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.factory.CraftApoli;

import org.bukkit.NamespacedKey;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Layer implements Serializable, FactoryInstance {
	@Serial
	private static final long serialVersionUID = 4L;

	NamespacedKey tag;
	DatapackFile layerFile;
	List<Origin> origins;

	public Layer(boolean toRegistry) {
		if (!toRegistry) {
			throw new RuntimeException("Invalid constructor used.");
		}
	}

	public Layer(NamespacedKey tag, DatapackFile layerFile, List<Origin> origins) {
		this.tag = tag;
		this.origins = origins;
		this.layerFile = layerFile;
	}

	/**
	 * @return The LayerContainer formatted for debugging, not to be used in other circumstances.
	 */
	@Override
	public String toString() {
		return "Tag = " + tag + " LayerFile = " + layerFile.toString();
	}

	@Override
	public NamespacedKey getKey() {
		return this.tag;
	}

	/**
	 * @return The tag associated with this layer
	 */
	public String getTag() {
		return tag.asString();
	}

	/**
	 * @return The file associated with this layer
	 */
	public DatapackFile getLayerFile() {
		return layerFile;
	}

	/**
	 * @return The name of the layer file or tag if null
	 */
	public String getName() {
		String name = (String) this.layerFile.get("name");
		if (name == null) return tag.asString();
		return name;
	}

	/**
	 * @return The name of the layer file or tag if null
	 */
	public boolean getReplace() {
		Boolean replace = (Boolean) this.layerFile.get("replace");
		if (replace == null) return false;
		return replace;
	}

	/**
	 * @return An array list of the loaded origins tags
	 */
	public ArrayList<String> getOrigins() {
		Object array = layerFile.get("origins");
		if (array instanceof JSONArray origins) return new ArrayList<String>(origins);
		return new ArrayList<>();
	}

	/**
	 * @param originTags Adds the specified originTags to the layer. If you only need to pass in one originTag use an array list with one tag.
	 */
	public void addOrigin(ArrayList<String> originTags) {
		this.layerFile.addOrigin(originTags);
	}

	@Override
	public List<FactoryObjectInstance> getValidObjectFactory() {
		return List.of(
			new FactoryObjectInstance("origins", JSONArray.class, null),
			new FactoryObjectInstance("enabled", Boolean.class, true),
			new FactoryObjectInstance("replace", Boolean.class, false),
			new FactoryObjectInstance("allow_random", Boolean.class, true),
			new FactoryObjectInstance("name", String.class, "No Name"),
			new FactoryObjectInstance("hidden", Boolean.class, false)
		);
	}

	@Override
	public void createInstance(FactoryProvider obj, File rawFile, Registrar<? extends Registerable> registry, NamespacedKey namespacedTag) {
		Registrar<Layer> registrar = (Registrar<Layer>) registry;
		List<Origin> list = new ArrayList<>();
		for (Object orRaw : ((JSONArray) obj.get("origins"))) {
			if (CraftApoli.getOrigin(orRaw.toString()) != null) {
				list.add(CraftApoli.getOrigin(orRaw.toString()));
			} else {
				CraftCalio.INSTANCE.getLogger().severe("Origin not found inside layer");
			}
		}
		registrar.register(new Layer(namespacedTag, new DatapackFile(obj.keySet().stream().toList(), obj.values().stream().toList()), list));
	}
}
