package me.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EquippedItemAction {

	public static void action(DeserializedFactoryJson data, Entity entity) {

		if (!(entity instanceof LivingEntity livingEntity)) {
			return;
		}

		EquipmentSlot slot = data.get("equipment_slot");
		Consumer<Tuple<Level, SlotAccess>> itemAction = data.get("action");

		SlotAccess stackReference = SlotAccess.forEquipmentSlot(livingEntity, slot);
		itemAction.accept(new Tuple<>(entity.level(), stackReference));

	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("equipped_item_action"),
			InstanceDefiner.instanceDefiner()
				.add("equipment_slot", SerializableDataTypes.EQUIPMENT_SLOT)
				.add("action", ApoliDataTypes.ITEM_ACTION),
			EquippedItemAction::action
		);
	}
}
