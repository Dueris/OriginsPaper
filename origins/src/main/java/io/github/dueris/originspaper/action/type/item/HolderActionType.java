package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.action.factory.ItemActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class HolderActionType {

	public static void action(ItemStack stack, Consumer<Entity> action) {

		Entity holder = ((EntityLinkedItemStack) stack).apoli$getEntity(true);

		if (holder != null) {
			action.accept(holder);
		}

	}

	public static ActionTypeFactory<Tuple<Level, SlotAccess>> getFactory() {
		return ItemActionTypeFactory.createItemStackBased(
			OriginsPaper.apoliIdentifier("holder_action"),
			new SerializableData()
				.add("action", ApoliDataTypes.ENTITY_ACTION),
			(data, worldAndStack) -> action(worldAndStack.getB(),
				data.get("action")
			)
		);
	}

}

