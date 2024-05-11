package me.dueris.genesismc.registry.registries;

import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.inst.FactoryInstance;
import me.dueris.calio.builder.inst.FactoryDataDefiner;
import me.dueris.calio.builder.inst.factory.FactoryBuilder;
import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonArray;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.screen.ScreenNavigator;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Layer extends FactoryJsonObject implements Serializable, FactoryInstance {
	@Serial
	private static final long serialVersionUID = 4L;

	NamespacedKey tag;
	List<Origin> origins;
	FactoryJsonObject factory;

	@ApiStatus.Internal
	public Layer(boolean toRegistry) {
		super(null);
		if (!toRegistry) {
			throw new RuntimeException("Invalid constructor used.");
		}
	}

	public Layer(NamespacedKey tag, List<Origin> origins, FactoryJsonObject factoryJsonObject) {
		super(factoryJsonObject.handle);
		this.tag = tag;
		this.origins = origins;
		this.factory = factoryJsonObject;
	}

	/**
	 * @return The LayerContainer formatted for debugging, not to be used in other circumstances.
	 */
	@Override
	public String toString() {
		return "Tag = " + tag;
	}

	public List<Origin> testChoosable(Entity entity) {
		List<Origin> tested = new ArrayList<Origin>();
		for (Origin origin : this.origins) {
			if (origin.getUsesCondition()) {
				if (ConditionExecutor.testEntity(origin.choosingCondition, (CraftEntity) entity)) {
					tested.add(origin);
				}
			} else {
				tested.add(origin);
			}
		}
		return tested;
	}

	public boolean testDefaultOrigin(Entity entity) {
		boolean autoChoose = this.getBooleanOrDefault("auto_choose", false);
		if (autoChoose) {
			if (entity instanceof Player p) {
				OriginPlayerAccessor.setOrigin(p, this, origins.get(0));
				return true;
			}
		}

		if (ScreenNavigator.orbChoosing.contains(entity)) return false; // Default origins dont apply on orb choosings
		if (this.isPresent("default_origin")) {
			NamespacedKey identifier = this.getNamespacedKey("default_origin");
			Origin origin = ((Registrar<Origin>) GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN)).get(identifier);
			if (origin != null && entity instanceof Player p) {
				OriginPlayerAccessor.setOrigin(p, this, origin);
				return true;
			}
		}
		return false;
	}

	public boolean isEnabled() {
		return this.getBooleanOrDefault("enabled", true);
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
	 * @return The name of the layer file or tag if null
	 */
	public String getName() {
		return getStringOrDefault("name", "No Name");
	}

	/**
	 * @return The name of the layer file or tag if null
	 */
	public boolean getReplace() {
		return getBooleanOrDefault("replace", false);
	}

	/**
	 * @return An array list of the loaded origins tags
	 */
	public List<String> getOriginIdentifiers() {
		return origins.stream().map(Origin::getTag).toList();
	}

	public List<Origin> getRandomOrigins() {
		boolean overrideUnchoosable = this.getBooleanOrDefault("allow_random_unchoosable", false);
		if (!this.getBooleanOrDefault("allow_random", false)) return new ArrayList<>();
		return this.origins.stream().filter(origin -> !origin.isUnchoosable() || overrideUnchoosable).filter(origin -> {
			if (this.isPresent("exclude_random")) {
				for (String identifier : this.getJsonArray("exclude_random").asList().stream().map(FactoryElement::getString).toList()) {
					if (origin.getTag().equalsIgnoreCase(identifier)) return false;
				}
			}
			return true; // It passes
		}).toList();
	}

	@Override
	public List<FactoryDataDefiner> getValidObjectFactory() {
		return List.of(
			new FactoryDataDefiner("origins", FactoryJsonArray.class, null),
			new FactoryDataDefiner("enabled", Boolean.class, true),
			new FactoryDataDefiner("replace", Boolean.class, false),
			new FactoryDataDefiner("allow_random", Boolean.class, true),
			new FactoryDataDefiner("hidden", Boolean.class, false)
		);
	}

	@Override
	public void createInstance(FactoryBuilder obj, File rawFile, Registrar<? extends Registrable> registry, NamespacedKey namespacedTag) {
		Registrar<Layer> registrar = (Registrar<Layer>) registry;
		AtomicBoolean merge = new AtomicBoolean(!obj.getRoot().getBooleanOrDefault("replace", false) && registry.rawRegistry.containsKey(namespacedTag));
		if (merge.get()) {
			List<Origin> originList = new ArrayList<>();
			for (FactoryElement element : obj.getRoot().getJsonArray("origins").asList()) {
				Origin origin = CraftApoli.getOrigin(element.getString());
				if (!origin.equals(CraftApoli.emptyOrigin())) {
					originList.add(origin);
				} else {
					CraftCalio.INSTANCE.getLogger().severe("Origin not found inside layer");
				}
			}
			registrar.get(namespacedTag).getOriginIdentifiers().stream().forEach(tag -> originList.add(CraftApoli.getOrigin(tag)));
			registrar.replaceEntry(namespacedTag, new Layer(namespacedTag, originList, obj.getRoot()));
		} else {
			List<Origin> list = new ArrayList<>();
			for (FactoryElement element : obj.getRoot().getJsonArray("origins").asList()) {
				if (element.isJsonObject()) {
					FactoryJsonObject jsonObject = element.toJsonObject();
					for (String elementString : jsonObject.getJsonArray("origins").asList().stream().map(FactoryElement::getString).toList()) {
						Origin origin = CraftApoli.getOrigin(elementString);
						if (!origin.equals(CraftApoli.emptyOrigin())) {
							origin.setUsesCondition(jsonObject.getJsonObject("condition"));
						} else {
							CraftCalio.INSTANCE.getLogger().severe("Origin(%a%) not found inside layer".replace("%a%", elementString));
						}
					}
				} else if (element.isString()) {
					Origin origin = CraftApoli.getOrigin(element.getString());
					if (!origin.equals(CraftApoli.emptyOrigin())) {
						list.add(origin);
					} else {
						CraftCalio.INSTANCE.getLogger().severe("Origin(%a%) not found inside layer".replace("%a%", element.getString()));
					}
				} else {
					CraftCalio.INSTANCE.getLogger().severe("Unknown type \"{t}\" was provided in the \"powers\" field for the OriginLayer!".replace("{t}", element.getClass().getSimpleName()));
				}
			}
			registrar.register(new Layer(namespacedTag, list, obj.getRoot()));
		}
	}
}
