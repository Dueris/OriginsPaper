package me.dueris.genesismc.registry.registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.FactoryHolder;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.screen.ScreenNavigator;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Layer implements FactoryHolder {
	private final int order;
	private final FactoryJsonArray origins;
	private final boolean enabled;
	private final boolean replace;
	private final String name;
	private final FactoryJsonObject guiTitle;
	private final String missingName;
	private final String missingDescription;
	private final boolean allowRandom;
	private final boolean allowRandomUnchoosable;
	private final FactoryJsonArray excludeRandom;
	private final NamespacedKey defaultOrigin;
	private final boolean autoChoose;
	private final boolean hidden;
	private final int loadingPriority;
	private boolean tagSet;
	private NamespacedKey tag;
	private String cachedTagString;

	public Layer(int order, FactoryJsonArray origins, boolean enabled, boolean replace, String name, FactoryJsonObject guiTitle, String missingName, String missingDescription,
				 boolean allowRandom, boolean allowRandomUnchoosable, FactoryJsonArray excludeRandom, NamespacedKey defaultOrigin, boolean autoChoose, boolean hidden, int loadingPriority
	) {
		this.order = order;
		this.origins = origins;
		this.enabled = enabled;
		this.replace = replace;
		this.name = name;
		this.guiTitle = guiTitle;
		this.missingName = missingName;
		this.missingDescription = missingDescription;
		this.allowRandom = allowRandom;
		this.allowRandomUnchoosable = allowRandomUnchoosable;
		this.excludeRandom = excludeRandom;
		this.defaultOrigin = defaultOrigin;
		this.autoChoose = autoChoose;
		this.hidden = hidden;
		this.loadingPriority = loadingPriority;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return data.add("order", int.class, 0)
			.add("origins", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()))
			.add("enabled", boolean.class, true)
			.add("replace", boolean.class, false)
			.add("name", String.class, "craftapoli.layer.name.not_found")
			.add("gui_title", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("missing_name", String.class, "Missing Name")
			.add("missing_description", String.class, "Missing Description")
			.add("allow_random", boolean.class, false)
			.add("allow_random_unchoosable", boolean.class, false)
			.add("exclude_random", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()))
			.add("default_origin", NamespacedKey.class, CraftApoli.emptyOrigin().getKey())
			.add("auto_choose", boolean.class, false)
			.add("hidden", boolean.class, false)
			.add("loading_priority", int.class, 0);
	}

	@Override
	public FactoryHolder ofResourceLocation(NamespacedKey key) {
		if (this.tagSet) return this;
		tagSet = true;
		this.tag = key;
		this.cachedTagString = key.asString();
		return this;
	}

	@Override
	public NamespacedKey getKey() {
		return this.tag;
	}

	public String getTag() {
		return cachedTagString;
	}

	public int getOrder() {
		return order;
	}

	public FactoryJsonArray getOrigins() {
		return origins;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isReplace() {
		return replace;
	}

	public String getName() {
		return name;
	}

	public FactoryJsonObject getGuiTitle() {
		return guiTitle;
	}

	public String getMissingName() {
		return missingName;
	}

	public String getMissingDescription() {
		return missingDescription;
	}

	public boolean isAllowRandom() {
		return allowRandom;
	}

	public boolean isAllowRandomUnchoosable() {
		return allowRandomUnchoosable;
	}

	public FactoryJsonArray getExcludeRandom() {
		return excludeRandom;
	}

	public NamespacedKey getDefaultOrigin() {
		return defaultOrigin;
	}

	public boolean isAutoChoose() {
		return autoChoose;
	}

	public boolean isHidden() {
		return hidden;
	}

	public int getLoadingPriority() {
		return loadingPriority;
	}

	public List<Origin> testChoosable(Entity entity) {
		List<Origin> tested = new ArrayList<Origin>();
		for (Origin origin : this.getOriginIdentifiers().stream()
			.map(CraftApoli::getOrigin)
			.filter(origin -> !origin.getTag().equalsIgnoreCase("origins:empty"))
			.toList()) {
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
		boolean autoChoose = isAutoChoose();
		if (autoChoose) {
			if (entity instanceof Player p) {
				FactoryElement element = getOrigins().asList().get(0);
				String identifier = element.getString();
				PowerHolderComponent.setOrigin(p, this, CraftApoli.getOrigin(identifier));
				return true;
			}
		}

		if (ScreenNavigator.orbChoosing.contains(entity)) return false; // Default origins dont apply on orb choosings
		if (!getDefaultOrigin().asString().equalsIgnoreCase("origins:empty")) {
			NamespacedKey identifier = getDefaultOrigin();
			Origin origin = ((Registrar<Origin>) GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN)).get(identifier);
			if (origin != null && entity instanceof Player p) {
				PowerHolderComponent.setOrigin(p, this, origin);
				return true;
			}
		}
		return false;
	}

	public List<String> getOriginIdentifiers() {
		List<String> identifiers = new ArrayList<>();
		getOrigins().asList().stream().forEach(element -> {
			if (element.isJsonObject()) {
				identifiers.addAll(element.toJsonObject().getJsonArray("origins").asList().stream().map(FactoryElement::getString).toList());
			} else if (element.isString()) {
				identifiers.add(element.getString());
			}
		});
		return identifiers;
	}

	public List<Origin> getRandomOrigins() {
		boolean overrideUnchoosable = isAllowRandomUnchoosable();
		if (!this.isAllowRandom()) return new ArrayList<>();
		return this.getOriginIdentifiers().stream().map(CraftApoli::getOrigin).filter(origin -> !origin.isUnchoosable() || overrideUnchoosable).filter(origin -> {
			if (!this.getExcludeRandom().asList().isEmpty()) {
				for (String identifier : this.getExcludeRandom().asList().stream().map(FactoryElement::getString).toList()) {
					if (origin.getTag().equalsIgnoreCase(identifier)) return false;
				}
			}
			return true; // It passes
		}).toList();
	}

	@Override
	public boolean canRegister() {
		Registrar<Layer> registrar = (Registrar<Layer>) GenesisMC.getPlugin().registry.retrieve(Registries.LAYER);
		AtomicBoolean merge = new AtomicBoolean(!isReplace() && registrar.rawRegistry.containsKey(this.tag));
		if (merge.get()) {
			List<Origin> originList = new ArrayList<>();
			for (FactoryElement element : getOrigins().asList()) {
				Origin origin = CraftApoli.getOrigin(element.getString());
				if (!origin.equals(CraftApoli.emptyOrigin())) {
					originList.add(origin);
				} else {
					CraftCalio.INSTANCE.getLogger().severe("Origin not found inside layer");
				}
			}
			Layer original = registrar.get(tag);
			original.getOriginIdentifiers().stream().forEach(tag -> originList.add(CraftApoli.getOrigin(tag)));
			original.origins.setEntries(
				Utils.toJsonStringArray(originList.stream().map(Origin::getTag).toList()).asList().stream().map(FactoryElement::new).toList()
			);
			return false;
		} else {
			for (FactoryElement element : getOrigins().asArray()) {
				if (element.isJsonObject()) {
					FactoryJsonObject jsonObject = element.toJsonObject();
					for (String elementString : this.getOriginIdentifiers()) {
						Origin origin = CraftApoli.getOrigin(elementString);
						if (!origin.equals(CraftApoli.emptyOrigin())) {
							origin.setUsesCondition(jsonObject.getJsonObject("condition"));
						} else {
							CraftCalio.INSTANCE.getLogger().severe("Origin(%a%) not found inside layer".replace("%a%", elementString));
						}
					}
				}
			}
		}
		return true;
	}
}
