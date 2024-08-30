package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class UsingItemConditionType {

	public static boolean condition(Entity entity, Predicate<Tuple<Level, ItemStack>> itemCondition) {

		if (!(entity instanceof LivingEntity living) || !living.isUsingItem()) {
			return false;
		}

		InteractionHand activeHand = living.getUsedItemHand();
		ItemStack stackInHand = living.getItemInHand(activeHand);

		return itemCondition.test(new Tuple<>(living.level(), stackInHand));

	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("using_item"),
			new SerializableData()
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null),
			(data, entity) -> condition(entity,
				data.getOrElse("item_condition", worldAndStack -> true))
		);
	}

}
