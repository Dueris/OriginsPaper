package me.dueris.originspaper.registry.registries;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.util.holder.TriPair;
import me.dueris.originspaper.factory.data.OriginsDataTypes;
import me.dueris.originspaper.factory.data.types.Impact;
import me.dueris.originspaper.factory.data.types.OriginUpgrade;
import me.dueris.originspaper.util.AsyncUpgradeTracker;
import me.dueris.originspaper.util.LangFile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class Origin {
	private final List<ResourceLocation> powers;
	private final net.minecraft.world.item.ItemStack icon;
	private final boolean unchoosable;
	private final int order;
	private final Impact impact;
	private final int loadingPriority;
	private final OriginUpgrade upgrade;
	private final TextComponent name;
	private final TextComponent description;
	private final ResourceLocation key;

	public Origin(@NotNull ResourceLocation key, List<ResourceLocation> powers, net.minecraft.world.item.ItemStack icon, boolean unchoosable, int order,
				  Impact impact, int loadingPriority, OriginUpgrade upgrade, net.minecraft.network.chat.Component name, net.minecraft.network.chat.Component description) {
		this.key = key;
		this.powers = powers;
		this.icon = icon;
		this.unchoosable = unchoosable;
		this.order = order;
		this.impact = impact;
		this.loadingPriority = loadingPriority;
		this.upgrade = upgrade;
		if (upgrade != null) {
			AsyncUpgradeTracker.upgrades.put(this, new TriPair<>(upgrade.advancementCondition(), upgrade.upgradeToOrigin(), upgrade.announcement()));
		}
		this.name = Component.text(
			LangFile.transform((name != null ? name.getString() : "origin.$namespace.$path.name")
				.replace("$namespace", key.getNamespace()).replace("$path", key.getPath()))
		);
		this.description = Component.text(
			LangFile.transform((description != null ? description.getString() : "origin.$namespace.$path.name")
				.replace("$namespace", key.getNamespace()).replace("$path", key.getPath()))
		);
	}

	public static InstanceDefiner buildDefiner() {
		return InstanceDefiner.instanceDefiner()
			.add("powers", SerializableDataTypes.list(SerializableDataTypes.IDENTIFIER), new LinkedList<>())
			.add("icon", SerializableDataTypes.ITEM_STACK, Items.PLAYER_HEAD.getDefaultInstance())
			.add("unchoosable", SerializableDataTypes.BOOLEAN, false)
			.add("order", SerializableDataTypes.INT, Integer.MAX_VALUE)
			.add("impact", OriginsDataTypes.IMPACT, Impact.NONE)
			.add("loading_priority", SerializableDataTypes.INT, 0)
			.add("upgrades", OriginsDataTypes.ORIGIN_UPGRADE, null)
			.add("name", SerializableDataTypes.TEXT, null)
			.add("description", SerializableDataTypes.TEXT, null);
	}

	public List<ResourceLocation> powers() {
		return powers;
	}

	public ItemStack icon() {
		return icon;
	}

	public boolean unchoosable() {
		return unchoosable;
	}

	public int order() {
		return order;
	}

	public Impact impact() {
		return impact;
	}

	public int loadingPriority() {
		return loadingPriority;
	}

	public TextComponent name() {
		return name;
	}

	public TextComponent description() {
		return description;
	}

	public @NotNull ResourceLocation key() {
		return key;
	}

	public @NotNull String getTag() {
		return key.toString();
	}

	@Nullable
	public OriginUpgrade getUpgrade() {
		return upgrade;
	}

	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "Origin[" +
			"powers=" + powers + ", " +
			"icon=" + icon + ", " +
			"unchoosable=" + unchoosable + ", " +
			"order=" + order + ", " +
			"impact=" + impact + ", " +
			"loadingPriority=" + loadingPriority + ", " +
			"name=" + name + ", " +
			"description=" + description + ']';
	}
}
