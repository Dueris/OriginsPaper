package io.github.dueris.originspaper.util;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.registry.DataObjectFactories;
import io.github.dueris.calio.registry.DataObjectFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.item.ItemStack;

public record IndexedStack(ItemStack stack, SlotRange slot) {

	public static final DataObjectFactory<IndexedStack> DATA_FACTORY = DataObjectFactory.simple(
		DataObjectFactories.ITEM_STACK.getSerializableData().copy()
			.add("slot", ApoliDataTypes.SINGLE_SLOT_RANGE),
		data -> new IndexedStack(
			DataObjectFactories.ITEM_STACK.fromData(data),
			data.get("slot")
		),
		(indexedStack, serializableData) -> DataObjectFactories.ITEM_STACK
			.toData(indexedStack.stack(), serializableData)
			.set("slot", indexedStack.slot())
	);

	public static final SerializableDataType<IndexedStack> DATA_TYPE = SerializableDataType.compound(
		DataObjectFactories.ITEM_STACK.getSerializableData().copy()
			.add("slot", ApoliDataTypes.SINGLE_SLOT_RANGE),
		data -> new IndexedStack(
			DataObjectFactories.ITEM_STACK.fromData(data),
			data.get("slot")
		),
		(indexedStack, serializableData) -> DataObjectFactories.ITEM_STACK.toData(indexedStack.stack(), serializableData)
			.set("slot", indexedStack.slot())
	);

	public IndexedStack {
		SlotRangesUtil.validateSingleSlot(slot).getOrThrow(IllegalArgumentException::new);
	}

	public int slotId() {
		return slot.slots().getFirst();
	}

}
