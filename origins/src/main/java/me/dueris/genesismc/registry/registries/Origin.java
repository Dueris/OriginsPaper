package me.dueris.genesismc.registry.registries;

import com.google.gson.JsonArray;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.FactoryHolder;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.storage.OriginConfiguration;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;

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
	private NamespacedKey tag = null;
	private String cachedTag = null;
	private boolean isDisabled;

	@Register
	public Origin(String name, String description, int impact, ItemStack icon, boolean unchoosable, FactoryJsonArray upgrades, FactoryJsonArray powers, int order, int loading_priority) {
		this.name = name;
		this.description = description;
		this.impact = impact;
		this.unchoosable = unchoosable;
		this.upgrades = upgrades;
		this.powers = powers;
		this.icon = icon;
		this.order = order;
		this.loadingPriority = loading_priority;
		this.powerIdentifiers.addAll(powers.asList().stream().map(FactoryElement::getString).map(NamespacedKey::fromString).filter(Objects::nonNull).map(CraftNamespacedKey::toMinecraft).toList());
	}

	public static FactoryData registerComponents(FactoryData data) {
		return data.add("name", String.class, "craftapoli.origin.name.not_found")
			.add("description", String.class, "craftapoli.origin.description.not_found")
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
				setDisabled();
				break;
			}
		}
	}

	public int getLoadingPriority() {
		return loadingPriority;
	}

	public FactoryJsonArray getPowerArray() {
		return powers;
	}

	public FactoryJsonArray getUpgrades() {
		return upgrades;
	}

	public boolean isUnchoosable() {
		return unchoosable || isDisabled;
	}

	public int getImpact() {
		return impact;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public boolean getUsesCondition() {
		return this.choosingCondition != null && !this.choosingCondition.isEmpty();
	}

	public void setUsesCondition(FactoryJsonObject condition) {
		this.choosingCondition = condition;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public int getOrder() {
		return order;
	}

	public Material getMaterialIcon() {
		return getIcon().getType();
	}

	public String getTag() {
		return this.tag.asString();
	}

	/**
	 * @return An array containing all the origin powers.
	 */
	public ArrayList<PowerType> getPowerContainers() {
		return new ArrayList<>(this.powerIdentifiers.stream().map(CraftNamespacedKey::fromMinecraft).map(NamespacedKey::asString).map(CraftApoli::getPowerFromTag).toList());
	}

	public List<String> getPowers() {
		return new ArrayList<>(this.powerIdentifiers.stream().map(CraftNamespacedKey::fromMinecraft).map(NamespacedKey::asString).toList());
	}

	private void setDisabled() {
		isDisabled = true;
	}

	@Override
	public Origin ofResourceLocation(NamespacedKey key) {
		if (this.tagSet) return this;
		tagSet = true;
		this.tag = key;
		this.cachedTag = key.asString();
		return this;
	}

	@Override
	public NamespacedKey getKey() {
		return this.tag;
	}
}
