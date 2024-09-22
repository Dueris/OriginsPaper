package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EquippedItemActionType {

	public static void action(Entity entity, EquipmentSlot slot, Consumer<Tuple<Level, SlotAccess>> itemAction) {

		if (!(entity instanceof LivingEntity livingEntity)) {
			return;
		}

		SlotAccess stackReference = SlotAccess.forEquipmentSlot(livingEntity, slot);
		itemAction.accept(new Tuple<>(entity.level(), stackReference));

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("equipped_item_action"),
			new SerializableData()
				.add("equipment_slot", SerializableDataTypes.EQUIPMENT_SLOT)
				.add("action", ApoliDataTypes.ITEM_ACTION),
			(data, entity) -> action(entity,
				data.get("equipment_slot"),
				data.get("action")
			)
		);
	}

}
