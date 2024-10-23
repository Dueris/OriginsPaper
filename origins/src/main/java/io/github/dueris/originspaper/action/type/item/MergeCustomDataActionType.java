package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.action.factory.ItemActionTypeFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class MergeCustomDataActionType {

	public static void action(ItemStack stack, CompoundTag nbt) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, oldNbt -> oldNbt.merge(nbt));
	}

	public static ActionTypeFactory<Tuple<Level, SlotAccess>> getFactory() {
		return ItemActionTypeFactory.createItemStackBased(
			OriginsPaper.apoliIdentifier("merge_custom_data"),
			new SerializableData()
				.add("nbt", SerializableDataTypes.NBT_COMPOUND),
			(data, worldAndStack) -> action(worldAndStack.getB(),
				data.get("nbt")
			)
		);
	}

}
