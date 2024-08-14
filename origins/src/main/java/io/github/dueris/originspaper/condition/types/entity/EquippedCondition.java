package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class EquippedCondition {

	public static boolean condition(@NotNull DeserializedFactoryJson data, Entity entity) {
		Predicate<Tuple<Level, ItemStack>> itemCondition = data.get("item_condition");
		return entity instanceof LivingEntity livingEntity
			&& itemCondition.test(new Tuple<>(livingEntity.level(), livingEntity.getItemBySlot(data.get("equipment_slot"))));
	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("equipped_item"),
			InstanceDefiner.instanceDefiner()
				.add("equipment_slot", SerializableDataTypes.EQUIPMENT_SLOT)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION),
			EquippedCondition::condition
		);
	}
}
