package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KeepInventoryPowerType extends PowerType implements Listener {

	public static final ObjectOpenHashSet<Integer> DEFAULT_SLOTS = ObjectOpenHashSet.of(SlotRanges.nameToIds("inventory.*"), SlotRanges.nameToIds("hotbar.*"), SlotRanges.nameToIds("armor.*"))
		.stream()
		.map(SlotRange::slots)
		.map(IntCollection::intStream)
		.flatMap(IntStream::boxed)
		.collect(Collectors.toCollection(ObjectOpenHashSet::new));

	public static final TypedDataObjectFactory<KeepInventoryPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
			.add("slots", ApoliDataTypes.SINGLE_SLOT_RANGES.optional(), Optional.empty()),
		(data, condition) -> new KeepInventoryPowerType(
			data.get("item_condition"),
			data.get("slots"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("item_condition", powerType.itemCondition)
			.set("slots", powerType.slotRanges)
	);

	public final Optional<ItemCondition> itemCondition;
	public final Optional<List<SlotRange>> slotRanges;

	public final ObjectOpenHashSet<Integer> slots;

	public KeepInventoryPowerType(Optional<ItemCondition> itemCondition, Optional<List<SlotRange>> slotRanges, Optional<EntityCondition> condition) {
		super(condition);

		this.itemCondition = itemCondition;
		this.slotRanges = slotRanges;

		this.slots = new ObjectOpenHashSet<>();

		this.slotRanges.stream()
			.flatMap(Collection::stream)
			.map(SlotRange::slots)
			.map(IntCollection::intStream)
			.flatMap(IntStream::boxed)
			.forEach(this.slots::add);

		this.slots.trim();

	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.KEEP_INVENTORY;
	}

}
