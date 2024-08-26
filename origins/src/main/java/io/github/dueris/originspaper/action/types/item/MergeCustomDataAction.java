package io.github.dueris.originspaper.action.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.action.ItemActionTypeFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MergeCustomDataAction {

	public static void action(@NotNull SerializableData.Instance data, @NotNull Tuple<Level, ItemStack> worldAndStack) {

		ItemStack stack = worldAndStack.getB();
		CompoundTag newNbt = data.get("nbt");

		CustomData.update(DataComponents.CUSTOM_DATA, stack, oldNbt -> oldNbt.merge(newNbt));

	}

	public static @NotNull ActionTypeFactory<Tuple<Level, SlotAccess>> getFactory() {
		return ItemActionTypeFactory.createItemStackBased(
			OriginsPaper.apoliIdentifier("merge_custom_data"),
			SerializableData.serializableData()
				.add("nbt", SerializableDataTypes.NBT_COMPOUND),
			MergeCustomDataAction::action
		);

	}
}
