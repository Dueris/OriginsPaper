package io.github.dueris.calio.registry;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.calio.mixin.ItemStackAccessor;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public final class DataObjectFactories {

	public static final DataObjectFactory<AttributeModifier> ATTRIBUTE_MODIFIER = DataObjectFactory.simple(
		new SerializableData()
			.add("id", SerializableDataTypes.IDENTIFIER)
			.add("amount", SerializableDataTypes.DOUBLE)
			.add("operation", SerializableDataTypes.MODIFIER_OPERATION),
		data -> new AttributeModifier(
			data.get("id"),
			data.get("amount"),
			data.get("operation")
		),
		(attributeModifier, serializableData) -> serializableData.instance()
			.set("id", attributeModifier.id())
			.set("amount", attributeModifier.amount())
			.set("operation", attributeModifier.operation())
	);

	public static final DataObjectFactory<ItemStack> UNCOUNTED_ITEM_STACK = DataObjectFactory.simple(
		new SerializableData()
			.add("id", SerializableDataTypes.ITEM_ENTRY)
			.add("components", SerializableDataTypes.COMPONENT_CHANGES, DataComponentPatch.EMPTY),
		data -> new ItemStack(
			data.get("id"), 1,
			data.get("components")
		),
		(stack, serializableData) -> serializableData.instance()
			.set("id", stack.getItemHolder())
			.set("components", stack.getComponentsPatch())
	);

	public static final DataObjectFactory<ItemStack> ITEM_STACK = DataObjectFactory.simple(
		UNCOUNTED_ITEM_STACK.getSerializableData().copy()
			.add("count", SerializableDataType.boundNumber(SerializableDataTypes.INT, 1, 99), 1),
		data -> {

			ItemStack stack = UNCOUNTED_ITEM_STACK.fromData(data);
			stack.setCount(data.getInt("count"));

			return stack;

		},
		(stack, serializableData) -> UNCOUNTED_ITEM_STACK
			.toData(stack, serializableData)
			.set("count", ((ItemStackAccessor) (Object) stack).getCountOverride())
	);

}
