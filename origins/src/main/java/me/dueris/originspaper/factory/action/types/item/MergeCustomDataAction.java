package me.dueris.originspaper.factory.action.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import me.dueris.originspaper.factory.action.ItemActionFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MergeCustomDataAction {

	public static void action(@NotNull DeserializedFactoryJson data, @NotNull Tuple<Level, ItemStack> worldAndStack) {

		ItemStack stack = worldAndStack.getB();
		CompoundTag newNbt = data.get("nbt");

		CustomData.update(DataComponents.CUSTOM_DATA, stack, oldNbt -> oldNbt.merge(newNbt));

	}

	public static @NotNull ActionFactory<Tuple<Level, SlotAccess>> getFactory() {
		return ItemActionFactory.createItemStackBased(
			OriginsPaper.apoliIdentifier("merge_custom_data"),
			InstanceDefiner.instanceDefiner()
				.add("nbt", SerializableDataTypes.NBT),
			MergeCustomDataAction::action
		);

	}
}
