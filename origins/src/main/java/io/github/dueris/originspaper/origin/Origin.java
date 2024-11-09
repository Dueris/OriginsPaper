package io.github.dueris.originspaper.origin;

import com.google.common.collect.ImmutableList;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.calio.util.Validatable;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.OriginComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.OriginsDataTypes;
import io.github.dueris.originspaper.power.MultiplePower;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerReference;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Origin implements Validatable {

	public static final Origin EMPTY = Origin.special(OriginsPaper.originIdentifier("empty"), ItemStack.EMPTY, Impact.NONE, Integer.MAX_VALUE);
	public static final SerializableDataType<Origin> DATA_TYPE = SerializableDataType.compound(
		new SerializableData()
			.add("id", SerializableDataTypes.IDENTIFIER)
			.add("icon", SerializableDataTypes.UNCOUNTED_ITEM_STACK, ItemStack.EMPTY)
			.add("powers", ApoliDataTypes.POWER_REFERENCE.list(), new ObjectArrayList<>())
			.add("upgrades", OriginsDataTypes.UPGRADES, new ObjectArrayList<>())
			.add("impact", OriginsDataTypes.IMPACT, Impact.NONE)
			.add("name", SerializableDataTypes.TEXT, null)
			.add("description", SerializableDataTypes.TEXT, null)
			.add("unchoosable", SerializableDataTypes.BOOLEAN, false)
			.add("order", SerializableDataTypes.INT, Integer.MAX_VALUE),
		data -> new Origin(
			data.get("id"),
			data.get("icon"),
			data.get("powers"),
			data.get("upgrades"),
			data.get("impact"),
			data.get("name"),
			data.get("description"),
			data.get("unchoosable"),
			data.get("order")
		),
		(origin, serializableData) -> serializableData.instance()
			.set("id", origin.getId())
			.set("icon", origin.getDisplayItem())
			.set("powers", origin.getPowerReferences())
			.set("upgrades", origin.upgrades)
			.set("impact", origin.getImpact())
			.set("name", origin.getName())
			.set("description", origin.getDescription())
			.set("unchoosable", !origin.isChoosable())
			.set("special", origin.isSpecial())
			.set("order", origin.getOrder())
	);

	private final ResourceLocation id;
	private final ItemStack displayItem;

	private final Set<PowerReference> powerReferences;
	private final Set<Power> powers;

	private final List<OriginUpgrade> upgrades;
	private final Impact impact;

	private final Component name;
	private final Component description;

	private final boolean choosable;
	private final boolean special;

	private final int order;

	protected Origin(ResourceLocation id, @NotNull ItemStack icon, List<PowerReference> powerReferences, List<OriginUpgrade> upgrades, Impact impact, @Nullable Component name, @Nullable Component description, boolean unchoosable, boolean special, int order) {

		this.id = id;
		this.displayItem = icon.copy();
		this.powerReferences = new ObjectLinkedOpenHashSet<>(powerReferences);
		this.powers = new ObjectLinkedOpenHashSet<>();
		this.upgrades = upgrades;
		this.impact = impact;
		this.name = name == null ? Component.translatable("origin." + id.getNamespace() + "." + id.getPath() + ".name") : name;
		this.description = description == null ? Component.translatable("origin." + id.getNamespace() + "." + id.getPath() + ".description") : description;
		this.choosable = !unchoosable;
		this.special = special;
		this.order = order;

	}

	public Origin(ResourceLocation id, ItemStack icon, List<PowerReference> powerReferences, List<OriginUpgrade> upgrades, Impact impact, @Nullable Component name, @Nullable Component description, boolean unchoosable, int order) {
		this(id, icon, powerReferences, upgrades, impact, name, description, unchoosable, false, order);
	}

	public static Origin special(ResourceLocation id, ItemStack icon, Impact impact, int order) {
		return new Origin(id, icon, new LinkedList<>(), new LinkedList<>(), impact, null, null, true, true, order);
	}

	public static void init() {

	}

	public static Map<OriginLayer, Origin> get(Entity entity) {
		if (entity instanceof Player) {
			return get((Player) entity);
		}
		return new HashMap<>();
	}

	public static Map<OriginLayer, Origin> get(Player player) {
		return OriginComponent.ORIGIN.get(player).getOrigins();
	}

	public ResourceLocation getId() {
		return id;
	}

	public ItemStack getDisplayItem() {
		return displayItem;
	}

	public ImmutableList<PowerReference> getPowerReferences() {
		return ImmutableList.copyOf(powerReferences);
	}

	public ImmutableList<Power> getPowers() {
		return ImmutableList.copyOf(powers);
	}

	@Deprecated(forRemoval = true)
	public Optional<OriginUpgrade> getUpgrade(AdvancementHolder advancement) {
		return upgrades.stream()
			.filter(ou -> ou.advancementCondition().equals(advancement.id()))
			.findFirst();
	}

	@Deprecated(forRemoval = true)
	public boolean hasUpgrade() {
		return !this.upgrades.isEmpty();
	}

	public Impact getImpact() {
		return impact;
	}

	public MutableComponent getName() {
		return name.copy();
	}

	public MutableComponent getDescription() {
		return description.copy();
	}

	public boolean isChoosable() {
		return this.choosable;
	}

	public boolean isSpecial() {
		return this.special;
	}

	public int getOrder() {
		return this.order;
	}

	@Override
	public void validate() {

		this.powers.clear();
		for (PowerReference powerReference : powerReferences) {

			try {
				powers.add(powerReference.getStrictReference());
			} catch (Exception e) {
				OriginsPaper.LOGGER.error("Origin \"{}\" contained unregistered power \"{}\"!", id, powerReference.getId());
			}

		}

	}

	public boolean hasPower(Power targetPower) {
		return powers.contains(targetPower) || powers
			.stream()
			.filter(MultiplePower.class::isInstance)
			.map(MultiplePower.class::cast)
			.map(MultiplePower::getSubPowerIds)
			.flatMap(Collection::stream)
			.anyMatch(targetPower.getId()::equals);
	}

	@Override
	public String toString() {

		StringBuilder str = new StringBuilder("Origin[id = " + id.toString() + ", powers = {");
		String separator = "";

		for (Power power : powers) {
			str.append(separator).append(power.getId());
			separator = ", ";
		}

		str.append("}]");
		return str.toString();

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof Origin other && this.id.equals(other.id));
	}

}
