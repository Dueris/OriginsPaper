package me.dueris.genesismc.registry.registries;

import com.google.gson.JsonArray;
import me.dueris.calio.builder.inst.FactoryData;
import me.dueris.calio.builder.inst.FactoryHolder;
import me.dueris.calio.builder.inst.factory.FactoryJsonArray;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RewriteOrigin implements FactoryHolder {

	private final String name;
	private final String description;
	private final int impact;
	private final boolean unchoosable;
	private final FactoryJsonArray upgrades;
	private final FactoryJsonArray powers;
	private final int loadingPriority;

	public RewriteOrigin(String name, String description, int impact, boolean unchoosable, FactoryJsonArray upgrades, FactoryJsonArray powers, int loading_priority) {
		this.name = name;
		this.description = description;
		this.impact = impact;
		this.unchoosable = unchoosable;
		this.upgrades = upgrades;
		this.powers = powers;
		this.loadingPriority = loading_priority;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return data.add("name", String.class, "craftapoli.name.not_found")
			.add("description", String.class, "craftapoli.description.not_found")
			.add("impact", Integer.class, 0)
			.add("icon", ItemStack.class, new ItemStack(Material.PLAYER_HEAD))
			.add("unchoosable", Boolean.class, false)
			.add("upgrades", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()))
			.add("powers", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()))
			.add("loading_priority", Integer.class, 0);
	}

	public int getLoadingPriority() {
		return loadingPriority;
	}

	public FactoryJsonArray getPowers() {
		return powers;
	}

	public FactoryJsonArray getUpgrades() {
		return upgrades;
	}

	public boolean isUnchoosable() {
		return unchoosable;
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
}
