package me.dueris.originspaper.registry.registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.FactoryHolder;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrar;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.screen.ScreenNavigator;
import me.dueris.originspaper.util.Util;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
	private final ResourceLocation defaultOrigin;
	private final boolean autoChoose;
	private final boolean hidden;
	private final int loadingPriority;
	private boolean tagSet;
	private ResourceLocation tag;
	private String cachedTagString;

	public Layer(int order, FactoryJsonArray origins, boolean enabled, boolean replace, String name, FactoryJsonObject guiTitle, String missingName, String missingDescription,
				 boolean allowRandom, boolean allowRandomUnchoosable, FactoryJsonArray excludeRandom, ResourceLocation defaultOrigin, boolean autoChoose, boolean hidden, int loadingPriority
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

	public static FactoryData registerComponents(@NotNull FactoryData data) {
		return data.add("order", int.class, 0)
			.add("origins", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()))
			.add("enabled", boolean.class, true)
			.add("replace", boolean.class, false)
			.add("name", String.class, "layer.$namespace.$path.name")
			.add("gui_title", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("missing_name", String.class, "Missing Name")
			.add("missing_description", String.class, "Missing Description")
			.add("allow_random", boolean.class, true)
			.add("allow_random_unchoosable", boolean.class, false)
			.add("exclude_random", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()))
			.add("default_origin", ResourceLocation.class, CraftApoli.emptyOrigin().key())
			.add("auto_choose", boolean.class, false)
			.add("hidden", boolean.class, false)
			.add("loading_priority", int.class, 0);
	}

	@Override
	public FactoryHolder ofResourceLocation(ResourceLocation key) {
		if (this.tagSet) {
			return this;
		} else {
			this.tagSet = true;
			this.tag = key;
			this.cachedTagString = key.toString();
			return this;
		}
	}

	@Override
	public ResourceLocation key() {
		return this.tag;
	}

	public String getTag() {
		return this.cachedTagString;
	}

	public int getOrder() {
		return this.order;
	}

	public FactoryJsonArray getOrigins() {
		return this.origins;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean isReplace() {
		return this.replace;
	}

	public String getName() {
		return this.name;
	}

	public FactoryJsonObject getGuiTitle() {
		return this.guiTitle;
	}

	public String getMissingName() {
		return this.missingName;
	}

	public String getMissingDescription() {
		return this.missingDescription;
	}

	public boolean isAllowRandom() {
		return this.allowRandom;
	}

	public boolean isAllowRandomUnchoosable() {
		return this.allowRandomUnchoosable;
	}

	public FactoryJsonArray getExcludeRandom() {
		return this.excludeRandom;
	}

	public ResourceLocation getDefaultOrigin() {
		return this.defaultOrigin;
	}

	public boolean isAutoChoose() {
		return this.autoChoose;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public int getLoadingPriority() {
		return this.loadingPriority;
	}

	public List<Origin> testChoosable(Entity entity) {
		List<Origin> tested = new ArrayList<>();

		for (Origin origin : this.getOriginIdentifiers()
			.stream()
			.map(CraftApoli::getOrigin)
			.filter(originx -> !originx.getTag().equalsIgnoreCase("origins:empty"))
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
		boolean autoChoose = this.isAutoChoose();
		if (autoChoose && entity instanceof Player p) {
			FactoryElement element = this.getOrigins().asList().get(0);
			String identifier = element.getString();
			PowerHolderComponent.setOrigin(p, this, CraftApoli.getOrigin(identifier));
			return true;
		} else if (ScreenNavigator.orbChoosing.contains(entity)) {
			return false;
		} else {
			if (!this.getDefaultOrigin().toString().equalsIgnoreCase("origins:empty")) {
				ResourceLocation identifier = this.getDefaultOrigin();
				Origin origin = OriginsPaper.getPlugin().registry.retrieve(Registries.ORIGIN).get(identifier);
				if (origin != null && entity instanceof Player p) {
					PowerHolderComponent.setOrigin(p, this, origin);
					return true;
				}
			}

			return false;
		}
	}

	public List<String> getOriginIdentifiers() {
		List<String> identifiers = new ArrayList<>();
		this.getOrigins().asList().stream().forEach(element -> {
			if (element.isJsonObject()) {
				identifiers.addAll(element.toJsonObject().getJsonArray("origins").asList().stream().map(FactoryElement::getString).toList());
			} else if (element.isString()) {
				identifiers.add(element.getString());
			}
		});
		return identifiers;
	}

	public List<Origin> getRandomOrigins() {
		return !this.isAllowRandom()
			? new ArrayList<>()
			: this.getOriginIdentifiers()
			.stream()
			.map(CraftApoli::getOrigin)
			.filter(origin -> !origin.isUnchoosable() || this.isAllowRandomUnchoosable())
			.filter(origin -> {
				if (!this.getExcludeRandom().asList().isEmpty()) {
					for (String identifier : this.getExcludeRandom().asList().stream().map(FactoryElement::getString).toList()) {
						if (origin.getTag().equalsIgnoreCase(identifier)) {
							return false;
						}
					}
				}

				return true;
			})
			.toList();
	}

	@Override
	public boolean canRegister() {
		Registrar<Layer> registrar = OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER);
		AtomicBoolean merge = new AtomicBoolean(!this.isReplace() && registrar.rawRegistry.containsKey(this.tag));
		if (merge.get()) {
			List<Origin> originList = new ArrayList<>();

			for (FactoryElement element : this.getOrigins().asList()) {
				Origin origin = CraftApoli.getOrigin(element.getString());
				if (!origin.equals(CraftApoli.emptyOrigin())) {
					originList.add(origin);
				} else {
					CraftCalio.INSTANCE.getLogger().severe("Origin not found inside layer");
				}
			}

			Layer original = registrar.get(this.tag);
			original.getOriginIdentifiers().stream().forEach(tag -> originList.add(CraftApoli.getOrigin(tag)));
			original.origins
				.setEntries(Util.toJsonStringArray(originList.stream().map(Origin::getTag).toList()).asList().stream().map(FactoryElement::new).toList());
			return false;
		} else {
			for (FactoryElement elementx : this.getOrigins().asArray()) {
				if (elementx.isJsonObject()) {
					FactoryJsonObject jsonObject = elementx.toJsonObject();

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

			return true;
		}
	}
}
