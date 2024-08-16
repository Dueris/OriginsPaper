package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class UsingItemCondition {

	public static boolean condition(SerializableData.Instance data, Entity entity) {

		if (!(entity instanceof LivingEntity livingEntity) || !livingEntity.isUsingItem()) {
			return false;
		}

		Predicate<Tuple<Level, ItemStack>> itemCondition = data.get("item_condition");
		InteractionHand activeHand = livingEntity.getUsedItemHand();

		ItemStack stackInHand = livingEntity.getItemInHand(activeHand);
		return itemCondition == null || itemCondition.test(new Tuple<>(livingEntity.level(), stackInHand));

	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("using_item"),
			SerializableData.serializableData()
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null),
			UsingItemCondition::condition
		);
	}
}
