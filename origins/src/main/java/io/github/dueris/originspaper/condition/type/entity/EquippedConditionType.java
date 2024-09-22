package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class EquippedConditionType {

	public static boolean condition(Entity entity, Predicate<Tuple<Level, ItemStack>> itemCondition, EquipmentSlot equipmentSlot) {
		return entity instanceof LivingEntity livingEntity
			&& itemCondition.test(new Tuple<>(livingEntity.level(), livingEntity.getItemBySlot(equipmentSlot)));
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("equipped_item"),
			new SerializableData()
				.add("equipment_slot", SerializableDataTypes.EQUIPMENT_SLOT)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION),
			(data, entity) -> condition(entity,
				data.get("item_condition"),
				data.get("equipment_slot")
			)
		);
	}

}
