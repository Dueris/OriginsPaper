package me.dueris.originspaper.registry.registries;

import com.google.gson.JsonArray;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.FactoryHolder;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.storage.OriginConfiguration;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Origin implements FactoryHolder {
	private final String name;
	private final String description;
	private final int impact;
	private final boolean unchoosable;
	private final FactoryJsonArray upgrades;
	private final FactoryJsonArray powers;
	private final int loadingPriority;
	private final ItemStack icon;
	private final int order;
	private final List<ResourceLocation> powerIdentifiers = new ArrayList<>();
	protected FactoryJsonObject choosingCondition;
	private boolean tagSet = false;
	private ResourceLocation tag = null;
	private String cachedTag = null;
	private boolean isDisabled;

	public Origin(String name, String description, int impact, ItemStack icon, boolean unchoosable, FactoryJsonArray upgrades, @NotNull FactoryJsonArray powers, int order, int loading_priority) {
		this.name = name;
		this.description = description;
		this.impact = impact;
		this.unchoosable = unchoosable;
		this.upgrades = upgrades;
		this.powers = powers;
		this.icon = icon;
		this.order = order;
		this.loadingPriority = loading_priority;
		this.powerIdentifiers.addAll(powers.asList().stream().map(FactoryElement::getString).map(ResourceLocation::parse).filter(Objects::nonNull).toList());
	}

	public static FactoryData registerComponents(@NotNull FactoryData data) {
		return data.add("name", String.class, "origin.$namespace.$path.name")
			.add("description", String.class, "origin.$namespace.$path.description")
			.add("impact", int.class, 0)
			.add("icon", ItemStack.class, new ItemStack(Material.PLAYER_HEAD))
			.add("unchoosable", boolean.class, false)
			.add("upgrades", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()))
			.add("powers", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()))
			.add("order", int.class, 0)
			.add("loading_priority", int.class, 0);
	}

	@Override
	public void bootstrap() {
		for (String origin : OriginConfiguration.getConfiguration().getStringList("disabled-origins")) {
			if (this.cachedTag.equalsIgnoreCase(origin)) {
				this.setDisabled();
				break;
			}
		}
	}

	public int getLoadingPriority() {
		return this.loadingPriority;
	}

	public FactoryJsonArray getPowerArray() {
		return this.powers;
	}

	public FactoryJsonArray getUpgrades() {
		return this.upgrades;
	}

	public boolean isUnchoosable() {
		return this.unchoosable || this.isDisabled;
	}

	public int getImpact() {
		return this.impact;
	}

	public String getDescription() {
		return this.description;
	}

	public String getName() {
		return this.name;
	}

	public boolean getUsesCondition() {
		return this.choosingCondition != null && !this.choosingCondition.isEmpty();
	}

	public void setUsesCondition(FactoryJsonObject condition) {
		this.choosingCondition = condition;
	}

	public ItemStack getIcon() {
		return this.icon;
	}

	public int getOrder() {
		return this.order;
	}

	public Material getMaterialIcon() {
		return this.getIcon().getType();
	}

	public String getTag() {
		return this.tag.toString();
	}

	public ArrayList<PowerType> getPowerContainers() {
		return new ArrayList<>(this.powerIdentifiers.stream().map(CraftApoli::getPowersFromResourceLocation).toList());
	}

	public List<String> getPowers() {
		return this.getPowerContainers().stream().filter(Objects::nonNull).map(PowerType::getTag).toList();
	}

	private void setDisabled() {
		this.isDisabled = true;
	}

	public Origin ofResourceLocation(ResourceLocation key) {
		if (this.tagSet) {
			return this;
		} else {
			this.tagSet = true;
			this.tag = key;
			this.cachedTag = key.toString();
			return this;
		}
	}

	@Override
	public ResourceLocation key() {
		return this.tag;
	}
}
